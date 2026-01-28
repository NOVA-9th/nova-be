package com.nova.nova_server.domain.member.service;

import com.nova.nova_server.domain.member.dto.MemberRequestDto;
import com.nova.nova_server.domain.member.dto.MemberResponseDto;
import com.nova.nova_server.domain.member.dto.MemberUpdateResponseDto;

public interface MemberService {

    //사용자 정보 조회
    MemberResponseDto getMemberInfo(Long memberId);

    //사용자 정보(이름) 수정
    MemberUpdateResponseDto updateMemberName(Long memberId, Long authenticatedMemberId, MemberRequestDto requestDto);
}