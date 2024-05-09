package com.ssafy.gateway.global;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenUtil {


//    @Value("${jwt.secret}")
//    private String secretKey;
public String secretKey = "ssafy1234"; // 환경 변수로 관리하는 것이 좋습니다.

    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
            .setSigningKey(secretKey.getBytes())
            .parseClaimsJws(token)
            .getBody();
        return Long.parseLong(claims.getSubject());
    }

    public String generateToken(String username) {
        return Jwts.builder()
//            .setClaims()
            .setSubject(username)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10시간 후 만료
            .signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
            .compact();
    }

    public boolean validateToken(String token, String username) {
        final String tokenUsername = getUsernameFromToken(token);
        return (username.equals(tokenUsername) && !isTokenExpired(token));
    }

    private Date getExpirationDateFromToken(String token) {
        return getAllClaimsFromToken(token).getExpiration();
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
            .setSigningKey(secretKey.getBytes())
            .parseClaimsJws(token)
            .getBody();
    }

    private boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public String getUsernameFromToken(String token) {
        return getAllClaimsFromToken(token).getSubject();
    }
}
