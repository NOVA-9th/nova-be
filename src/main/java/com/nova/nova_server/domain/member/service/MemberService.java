package com.nova.nova_server.domain.member.service;

import com.nova.nova_server.domain.interest.entity.Interest;
import com.nova.nova_server.domain.interest.repository.InterestRepository;
import com.nova.nova_server.domain.keyword.error.KeywordErrorCode;
import com.nova.nova_server.domain.member.dto.*;
import com.nova.nova_server.domain.member.entity.Member;
import com.nova.nova_server.domain.keyword.entity.Keyword;
import com.nova.nova_server.domain.keyword.repository.KeywordRepository;
import com.nova.nova_server.domain.member.entity.MemberPreferInterest;
import com.nova.nova_server.domain.member.entity.MemberPreferKeyword;
import com.nova.nova_server.domain.member.error.MemberErrorCode;
import com.nova.nova_server.domain.member.repository.MemberPreferInterestRepository;
import com.nova.nova_server.domain.member.repository.MemberPreferKeywordRepository;
import com.nova.nova_server.domain.member.repository.MemberRepository;
import com.nova.nova_server.domain.member.repository.MemberProfileImageRepository;
import com.nova.nova_server.global.apiPayload.exception.NovaException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberProfileImageRepository memberProfileImageRepository;
    private final MemberPreferKeywordRepository memberPreferKeywordRepository;
    private final MemberPreferInterestRepository memberPreferInterestRepository;
    private final KeywordRepository keywordRepository;
    private final InterestRepository interestRepository;

    @Transactional(readOnly = true)
    public MemberResponseDto getMemberInfo(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NovaException(MemberErrorCode.MEMBER_NOT_FOUND));

        String profileImageUrl = memberProfileImageRepository.findById(memberId)
                .map(image -> "/api/members/" + memberId + "/profile-image")
                .orElse(null);

        return MemberResponseDto.builder()
                .id(member.getId())
                .name(member.getName())
                .email(member.getEmail())
                .profileImage(profileImageUrl)
                .build();
    }

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

    public void deleteMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NovaException(MemberErrorCode.MEMBER_NOT_FOUND));

        if (memberProfileImageRepository.existsById(memberId)) {
            memberProfileImageRepository.deleteById(memberId);
        }

        memberRepository.delete(member);
    }

    @Transactional(readOnly = true)
    public MemberConnectedAccountsResponseDto getConnectedAccounts(Long memberId) {
        // 1. 회원 조회 (없으면 MEMBER_NOT_FOUND 예외 발생)
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NovaException(MemberErrorCode.MEMBER_NOT_FOUND));

        // 2. 각 소셜 ID가 null이 아니면 연결된 것으로 판단하여 반환
        return MemberConnectedAccountsResponseDto.builder()
                .googleConnected(member.getGoogleId() != null)
                .kakaoConnected(member.getKakaoId() != null)
                .githubConnected(member.getGithubId() != null)
                .build();
    }

    public MemberResponseDto createTestMember() {
        Member mockUser = Member.builder()
                .id(2L)
                .name("테스트 유저")
                .email("test@example.com")
                .level(Member.MemberLevel.NOVICE)
                .background("테스트 배경")
                .googleId("test-google-id")
                .build();

        memberRepository.save(mockUser);
        return getMemberInfo(2L);
    }
}