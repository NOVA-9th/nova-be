package com.nova.nova_server.domain.auth.filter;

import com.nova.nova_server.domain.member.entity.Member;
import com.nova.nova_server.domain.member.repository.MemberRepository;
import com.nova.nova_server.domain.auth.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;
	private final MemberRepository memberRepository;

	private static final String AUTHORIZATION_HEADER = "Authorization";
	private static final String BEARER_PREFIX = "Bearer ";

	@Override
	protected void doFilterInternal(
		@NonNull HttpServletRequest request,
		@NonNull HttpServletResponse response,
		@NonNull FilterChain filterChain
	) throws ServletException, IOException {
		String token = resolveToken(request);

		if (StringUtils.hasText(token) && jwtUtil.validateToken(token)) {
			try {
				Long memberId = jwtUtil.getMemberIdFromToken(token);
				if (memberId == null) {
					filterChain.doFilter(request, response);
					return;
				}

				Member.MemberRole role = jwtUtil.getRoleFromToken(token);
				Authentication authentication = createAuthentication(memberId, role);
				SecurityContextHolder.getContext().setAuthentication(authentication);
			} catch (Exception e) {
				log.error("JWT 인증 처리 중 오류 발생", e);
			}
		}

		filterChain.doFilter(request, response);
	}

	private String resolveToken(HttpServletRequest request) {
		String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
			return bearerToken.substring(BEARER_PREFIX.length());
		}
		return null;
	}

	private Authentication createAuthentication(@NonNull Long memberId, Member.MemberRole tokenRole) {
		Member.MemberRole role = tokenRole != null
			? tokenRole
			: memberRepository.findById(memberId)
				.map(member -> member.getRole() != null ? member.getRole() : Member.MemberRole.USER)
				.orElse(Member.MemberRole.USER);

		return new UsernamePasswordAuthenticationToken(
			memberId,
			null,
			Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()))
		);
	}
}
