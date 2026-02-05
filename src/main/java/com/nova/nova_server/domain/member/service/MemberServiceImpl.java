package com.nova.nova_server.domain.member.service;

import com.nova.nova_server.domain.interest.entity.Interest;
import com.nova.nova_server.domain.interest.repository.InterestRepository;
import com.nova.nova_server.domain.keyword.error.KeywordErrorCode;
import com.nova.nova_server.domain.member.dto.MemberPersonalizationDto;
import com.nova.nova_server.domain.member.dto.MemberRequestDto;
import com.nova.nova_server.domain.member.dto.MemberResponseDto;
import com.nova.nova_server.domain.member.dto.MemberUpdateResponseDto;
import com.nova.nova_server.domain.member.entity.Member;
import com.nova.nova_server.domain.keyword.entity.Keyword;
import com.nova.nova_server.domain.keyword.repository.KeywordRepository;
import com.nova.nova_server.domain.member.entity.MemberPreferInterest;
import com.nova.nova_server.domain.member.entity.MemberPreferKeyword;
import com.nova.nova_server.domain.member.error.MemberErrorCode;
import com.nova.nova_server.domain.member.repository.MemberPreferInterestRepository;
import com.nova.nova_server.domain.member.repository.MemberPreferKeywordRepository;
import com.nova.nova_server.domain.member.repository.MemberRepository;
import com.nova.nova_server.domain.member.util.ImageProcessor;
import com.nova.nova_server.global.apiPayload.exception.NovaException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final MemberPreferKeywordRepository memberPreferKeywordRepository;
    private final MemberPreferInterestRepository memberPreferInterestRepository;
    private final KeywordRepository keywordRepository;
    private final ImageProcessor imageProcessor;
    private final InterestRepository interestRepository;

    @Override
    @Transactional(readOnly = true)
    public MemberResponseDto getMemberInfo(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NovaException(MemberErrorCode.MEMBER_NOT_FOUND));

        String profileImageUrl = (member.getProfileImage() != null)
                ? "/api/members/" + member.getId() + "/profile-image"
                : null;

        return MemberResponseDto.builder()
                .id(member.getId())
                .name(member.getName())
                .email(member.getEmail())
                .profileImage(profileImageUrl)
                .build();
    }

    @Override
    public MemberUpdateResponseDto updateMemberName(Long memberId, Long authenticatedMemberId, MemberRequestDto requestDto) {
        //본인 확인 로직
        if (!memberId.equals(authenticatedMemberId)) {
            throw new NovaException(MemberErrorCode.MEMBER_FORBIDDEN);
        }

        //이름 null 및 빈 문자열 체크 로직 수정
        if (requestDto.getName() == null || requestDto.getName().trim().isEmpty()) {
            throw new NovaException(MemberErrorCode.MEMBER_NAME_REQUIRED);
        }

        //회원 조회 및 수정
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NovaException(MemberErrorCode.MEMBER_NOT_FOUND));

        member.setName(requestDto.getName());

        return MemberUpdateResponseDto.builder()
                .id(member.getId())
                .name(member.getName())
                .build();
    }

    @Override
    public byte[] uploadProfileImageRaw(Long memberId, MultipartFile file) throws IOException {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NovaException(MemberErrorCode.MEMBER_NOT_FOUND));

        log.info("Original size: {} bytes", file.getSize());

        byte[] compressedImage = imageProcessor.compressImage(file);
        member.updateProfileImage(compressedImage);

        return compressedImage;
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] getProfileImageRaw(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NovaException(MemberErrorCode.MEMBER_NOT_FOUND));

        if (member.getProfileImage() == null) {
            return null;
        }
        return member.getProfileImage().getImage();
    }

    @Override
    public void deleteProfileImage(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NovaException(MemberErrorCode.MEMBER_NOT_FOUND));

        member.updateProfileImage(null);
    }

    @Override
    public MemberPersonalizationDto getMemberPersonalization(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NovaException(MemberErrorCode.MEMBER_NOT_FOUND));

        List<String> keywords = memberPreferKeywordRepository.findKeywordNamesByMemberId(memberId);

        return MemberPersonalizationDto.builder()
                .level(member.getLevel())
                .background(member.getBackground())
                .interests(member.getPreferInterests()
                        .stream()
                        .map(preferInterest -> preferInterest.getInterest().getId())
                        .toList())
                .keywords(keywords)
                .build();
    }

    @Override
    @Transactional
    public void updateMemberPersonalization(Long memberId, MemberPersonalizationDto memberPersonalizationDto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NovaException(MemberErrorCode.MEMBER_NOT_FOUND));

        // 수준
        member.setLevel(memberPersonalizationDto.level());

        // 배경
        member.setBackground(memberPersonalizationDto.background());

        // 관심분야
        List<Long> interestIds = memberPersonalizationDto.interests();
        updateMemberInterests(member, interestIds);

        // 키워드
        List<String> keywordNames = memberPersonalizationDto.keywords();
        updateMemberKeywords(member, keywordNames);
    }

    private void updateMemberInterests(Member member, List<Long> interestIds) {
        memberPreferInterestRepository.deleteByMember(member);

        if (interestIds == null || interestIds.isEmpty()) {
            return;
        }

        List<Interest> interests = interestRepository.findByIdIn(interestIds);
        if (interests.size() != interestIds.size()) {
            throw new NovaException(KeywordErrorCode.INTEREST_NOT_FOUND_BAD_REQUEST);
        }

        List<MemberPreferInterest> preferInterests = interests.stream()
                .map(interest -> MemberPreferInterest.builder()
                        .member(member)
                        .interest(interest)
                        .build())
                .toList();

        memberPreferInterestRepository.saveAll(preferInterests);
    }

    private void updateMemberKeywords(Member member, List<String> keywordNames) {
        memberPreferKeywordRepository.deleteByMember(member);

        if (keywordNames == null || keywordNames.isEmpty()) {
            return;
        }

        List<Keyword> keywords = keywordRepository.findByNameIn(keywordNames);
        if (keywords.size() != keywordNames.size()) {
            throw new NovaException(KeywordErrorCode.KEYWORD_NOT_FOUND_BAD_REQUEST);
        }

        List<MemberPreferKeyword> preferKeywords = keywords.stream()
                .map(keyword -> MemberPreferKeyword.builder()
                        .member(member)
                        .keyword(keyword)
                        .build())
                .toList();

        memberPreferKeywordRepository.saveAll(preferKeywords);
    }

    @Override
    public void deleteMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NovaException(MemberErrorCode.MEMBER_NOT_FOUND));

        memberRepository.delete(member);
    }
}