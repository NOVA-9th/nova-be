package com.nova.nova_server.domain.member.service;

import com.nova.nova_server.domain.member.dto.MemberPersonalizationDto;
import com.nova.nova_server.domain.member.dto.MemberRequestDto;
import com.nova.nova_server.domain.member.dto.MemberResponseDto;
import com.nova.nova_server.domain.member.dto.MemberUpdateResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface MemberService {

    //사용자 정보 조회
    MemberResponseDto getMemberInfo(Long memberId);

    //사용자 정보(이름) 수정
    MemberUpdateResponseDto updateMemberName(Long memberId, Long authenticatedMemberId, MemberRequestDto requestDto);

    byte[] getProfileImageRaw(Long memberId);

    // 바이너리 응답용 메서드 추가
    byte[] uploadProfileImageRaw(Long memberId, MultipartFile file) throws IOException;

    //프로필 이미지 삭제
    void deleteProfileImage(Long memberId);

    MemberPersonalizationDto getMemberPersonalization(Long memberId);

    void updateMemberPersonalization(Long memberId, MemberPersonalizationDto memberPersonalizationDto);
}