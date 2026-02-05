package com.nova.nova_server.domain.member.dto;

import com.nova.nova_server.domain.member.entity.Member;
import lombok.Builder;

import java.util.List;

@Builder
public record MemberPersonalizationDto(
        Member.MemberLevel level,
        String background,
        List<Long> interests,
        List<String> keywords
) {
}
