package com.nova.nova_server.domain.member.service;

import com.nova.nova_server.domain.member.dto.MemberResponseDto;

public interface MemberService {

    //사용자 정보 조회
    MemberResponseDto getMemberInfo(Long memberId);
    //사용자 정보 수정
}
