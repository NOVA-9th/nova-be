package com.nova.nova_server.domain.auth.util;

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

	public String generateToken(Long memberId) {
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + jwtConfig.getExpiration());

		SecretKey key = Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8));

		return Jwts.builder()
			.subject(String.valueOf(memberId))
			.issuedAt(now)
			.expiration(expiryDate)
			.signWith(key)
			.compact();
	}

	public String generateTokenWithExpiration(Long memberId, long expirationMillis) {
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + expirationMillis);

		SecretKey key = Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8));

		return Jwts.builder()
			.subject(String.valueOf(memberId))
			.issuedAt(now)
			.expiration(expiryDate)
			.signWith(key)
			.compact();
	}

	public Long getMemberIdFromToken(String token) {
		SecretKey key = Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8));

		Claims claims = Jwts.parser()
			.verifyWith(key)
			.build()
			.parseSignedClaims(token)
			.getPayload();

		return Long.parseLong(claims.getSubject());
	}

	public boolean validateToken(String token) {
		try {
			SecretKey key = Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8));
			Jwts.parser()
				.verifyWith(key)
				.build()
				.parseSignedClaims(token);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
