package com.nova.nova_server.domain.member.service;

import com.nova.nova_server.domain.member.dto.MemberConnectedAccountsResponseDto;
import com.nova.nova_server.domain.member.dto.MemberRequestDto;
import com.nova.nova_server.domain.member.dto.MemberResponseDto;
import com.nova.nova_server.domain.member.dto.MemberUpdateResponseDto;
import com.nova.nova_server.domain.member.entity.Member;
import com.nova.nova_server.domain.member.error.MemberErrorCode;
import com.nova.nova_server.domain.member.repository.MemberRepository;
import com.nova.nova_server.domain.member.util.ImageProcessor;
import com.nova.nova_server.global.apiPayload.exception.NovaException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final ImageProcessor imageProcessor;

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

        member.updateName(requestDto.getName());

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
    public void deleteMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NovaException(MemberErrorCode.MEMBER_NOT_FOUND));

        memberRepository.delete(member);
    }

    @Override
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
}