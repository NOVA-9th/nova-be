package com.nova.nova_server.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberConnectedAccountsResponseDto {
    private boolean googleConnected;
    private boolean kakaoConnected;
    private boolean githubConnected;
}