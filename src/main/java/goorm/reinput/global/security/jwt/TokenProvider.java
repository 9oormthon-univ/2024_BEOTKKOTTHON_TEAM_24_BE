package goorm.reinput.global.security.jwt;

import goorm.reinput.global.auth.PrincipalDetails;
import org.springframework.beans.factory.annotation.Value;

import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.Date;

public class TokenProvider {
    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.accessToken.expiration}")
    private Long accessTokenExpiration;

    @Value("${app.jwt.refreshToken.expiration}")
    private Long refreshTokenExpiration;


    //Access 토큰 생성
    public String generateAccessToken(PrincipalDetails principalDetails) {
        byte[] signingKey = jwtSecret.getBytes(StandardCharsets.UTF_8);

        return Jwts.builder()
                .signWith(Keys.hmacShaKeyFor(signingKey), SignatureAlgorithm.HS512)
                .setExpiration(Date.from(ZonedDateTime.now().plusMinutes(accessTokenExpiration).toInstant()))
                .setSubject(principalDetails.getUserId())
                .claim("type", TokenType.ACCESS)
                .compact();
    }

    //Refresh 토큰 생성
    public String generateRefreshToken(PrincipalDetails principalDetails) {
        byte[] signingKey = jwtSecret.getBytes(StandardCharsets.UTF_8);

        return Jwts.builder()
                .signWith(Keys.hmacShaKeyFor(signingKey), SignatureAlgorithm.HS512)
                .setExpiration(Date.from(ZonedDateTime.now().plusDays(refreshTokenExpiration).toInstant()))
                .setSubject(principalDetails.getUserId())
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

            // Parse the token. If it's invalid, an exception will be thrown
            Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token);

            // If no exception is thrown, the token is valid
            return true;
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | IllegalArgumentException e) {
            // Log the exception (or handle it as needed)
            log.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }
}
