package com.nova.nova_server.domain.member.service;

import com.nova.nova_server.domain.member.dto.MemberRequestDto;
import com.nova.nova_server.domain.member.dto.MemberResponseDto;
import com.nova.nova_server.domain.member.dto.MemberUpdateResponseDto;
import com.nova.nova_server.domain.member.entity.Member;
import com.nova.nova_server.domain.member.repository.MemberRepository;
import org.springframework.transaction.annotation.Transactional;
import com.nova.nova_server.domain.member.util.ImageProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final ImageProcessor imageProcessor;

    //사용자 정보 조회 로직
    @Override
    public MemberResponseDto getMemberInfo(Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 프로필 이미지가 없으면 null, 있으면 이미지 URL 경로 반환
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

    //사용자 이름 수정 로직
    @Override
    public MemberUpdateResponseDto updateMemberName(Long memberId, Long authenticatedMemberId, MemberRequestDto requestDto) {

        // 다른 사람의 이름을 수정하는 것을 방지
        if (!memberId.equals(authenticatedMemberId)) {
            throw new IllegalArgumentException("본인의 정보만 수정할 수 있습니다.");
        }

        //2. 이름 null 체크
        if (requestDto.getName() == null || requestDto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("이름은 필수입니다.");
        }

        // 회원 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 이름 수정
        member.updateName(requestDto.getName());

        // 응답 DTO 생성
        return MemberUpdateResponseDto.builder()
                .id(member.getId())
                .name(member.getName())
                .build();
    }

    //프로필 이미지 업로드 & 수정(PUT도 해당 메서드 사용)
    @Override
    public byte[] uploadProfileImage(Long memberId, MultipartFile file) throws IOException {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 이미지 압축 (ImageProcessor 사용)
        byte[] compressedImage = imageProcessor.compressImage(file);

        // DB에 저장(덮어쓰기)
        member.updateProfileImage(compressedImage);

        return compressedImage;
    }

    //프로필 이미지 조회
    @Override
    @Transactional(readOnly = true)
    public byte[] getProfileImage(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        return member.getProfileImage();
    }

    //프로필 이미지 삭제
    @Override
    public void deleteProfileImage(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // null로 업데이트하여 이미지 데이터 제거
        member.updateProfileImage(null);
    }
}