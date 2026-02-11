package com.nova.nova_server.domain.auth.util;

import com.nova.nova_server.domain.member.entity.Member;
import com.nova.nova_server.global.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtUtil {

	private final JwtConfig jwtConfig;
	private static final String ROLE_CLAIM_KEY = "role";

	/// JwtConfig 에 설정된 expiration 값의 수명을 가지는 JWT 생성
	public String generateToken(Long memberId) {
		return generateToken(memberId, Member.MemberRole.USER, jwtConfig.getExpiration());
	}

	/// JwtConfig 에 설정된 expiration 값의 수명을 가지는 JWT 생성
	public String generateToken(Long memberId, Member.MemberRole role) {
		return generateToken(memberId, role, jwtConfig.getExpiration());
	}

	/// expirationMillis 의 수명을 가지는 JWT 생성
	public String generateToken(Long memberId, long expirationMillis) {
		return generateToken(memberId, Member.MemberRole.USER, expirationMillis);
	}

	/// expirationMillis 의 수명을 가지는 JWT 생성
	public String generateToken(Long memberId, Member.MemberRole role, long expirationMillis) {
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + expirationMillis);

		SecretKey key = Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8));

		return Jwts.builder()
			.subject(String.valueOf(memberId))
			.claim(ROLE_CLAIM_KEY, role != null ? role.name() : Member.MemberRole.USER.name())
			.issuedAt(now)
			.expiration(expiryDate)
			.signWith(key)
			.compact();
	}

	public Long getMemberIdFromToken(String token) {
		Claims claims = parseClaims(token);
		return Long.parseLong(claims.getSubject());
	}

	public Member.MemberRole getRoleFromToken(String token) {
		Claims claims = parseClaims(token);
		Object roleClaim = claims.get(ROLE_CLAIM_KEY);
		if (roleClaim == null) {
			return null;
		}

		try {
			return Member.MemberRole.valueOf(roleClaim.toString());
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	public boolean validateToken(String token) {
		try {
			parseClaims(token);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private Claims parseClaims(String token) {
		SecretKey key = Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8));
		return Jwts.parser()
			.verifyWith(key)
			.build()
			.parseSignedClaims(token)
			.getPayload();
	}
}
