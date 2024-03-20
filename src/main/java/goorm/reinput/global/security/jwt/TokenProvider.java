package goorm.reinput.global.security.jwt;

import goorm.reinput.global.auth.PrincipalDetails;
import goorm.reinput.global.domain.TokenType;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.Date;

@Slf4j
@Component
public class TokenProvider {
    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.accessToken.expiration}")
    private Long accessTokenExpiration;

    @Value("${app.jwt.refreshToken.expiration}")
    private Long refreshTokenExpiration;


    //Access 토큰 생성
    public String generateAccessToken(Long userId) {
        byte[] signingKey = jwtSecret.getBytes(StandardCharsets.UTF_8);

        return Jwts.builder()
                .signWith(Keys.hmacShaKeyFor(signingKey), SignatureAlgorithm.HS512)
                .setExpiration(Date.from(ZonedDateTime.now().plusMinutes(accessTokenExpiration).toInstant()))
                .setSubject(userId.toString())
                .claim("type", TokenType.ACCESS)
                .compact();
    }

    //Refresh 토큰 생성
    public String generateRefreshToken(Long userId) {
        byte[] signingKey = jwtSecret.getBytes(StandardCharsets.UTF_8);

        return Jwts.builder()
                .signWith(Keys.hmacShaKeyFor(signingKey), SignatureAlgorithm.HS512)
                .setExpiration(Date.from(ZonedDateTime.now().plusDays(refreshTokenExpiration).toInstant()))
                .setSubject(userId.toString())
                .claim("type", TokenType.REFRESH)
                .compact();
    }
    //토큰에서 username 추출
    public String getUserIdByToken(String token) {
        byte[] signingKey = jwtSecret.getBytes(StandardCharsets.UTF_8);

        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
    //토큰 타입 반환
    public String getTokenTypeByToken(String token) {
        byte[] signingKey = jwtSecret.getBytes(StandardCharsets.UTF_8);

        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("type")
                .toString();
    }
    //token 유효성 검사
    public boolean validateToken(String token) {
        try {
            log.info("===== JWT Token validating - TokenProvider ======");
            byte[] signingKey = jwtSecret.getBytes(StandardCharsets.UTF_8);

            // 토큰을 파싱
            Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token);

            // 예외가 없다면 유효한 토큰
            return true;
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | IllegalArgumentException e) {
            //todo: Custom Exception (필요시)
            log.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }
}
