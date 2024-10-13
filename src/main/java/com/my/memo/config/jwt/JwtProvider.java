package com.my.memo.config.jwt;

import com.my.memo.domain.user.User;
import com.my.memo.ex.CustomJwtException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtProvider {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${secret.key}")
    private String SECRET_KEY;
    private Key key;

    public String create(User user) {
        return JwtVo.TOKEN_PREFIX + Jwts.builder()
                .setSubject("Memo: " + user.getName())
                .setExpiration(new Date(System.currentTimeMillis() + JwtVo.EXPIRATION_TIME))
                .claim("id", user.getId())
                .claim("role", user.getRole())
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }


    public String getUserRole(String token) {
        Claims claims = getClaims(token);
        return claims.get("role", String.class);
    }

    public Long getUserId(String token) {
        Claims claims = getClaims(token);
        return claims.get("id", Long.class);
    }

    private Claims getClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

        } catch (Exception e) {
            log.warn("유효하지 않은 토큰: ", e);
            throw new CustomJwtException(HttpStatus.UNAUTHORIZED.value(), "유효하지 않은 토큰입니다");
        }
    }


    // 토큰 substring
    public String substringToken(String tokenValue) {
        if (StringUtils.hasText(tokenValue) && tokenValue.startsWith(JwtVo.TOKEN_PREFIX)) {
            return tokenValue.substring(7);
        }
        log.warn("토큰을 찾을 수 없음");
        throw new CustomJwtException(HttpStatus.NOT_FOUND.value(), "토큰을 찾을 수 없습니다");
    }

    public void addJwtToHeader(String token, HttpServletResponse res) {
        res.setHeader(JwtVo.HEADER, token);
    }

    @PostConstruct //빈 등록될 때 딱 한번만 호출
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(SECRET_KEY);
        key = Keys.hmacShaKeyFor(bytes);
    }

    // 토큰 검증
    public boolean validateToken(String token) throws CustomJwtException {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException | SignatureException e) {
            log.warn("Invalid JWT signature: 유효하지 않은 JWT 서명");
            throw new CustomJwtException(HttpStatus.UNAUTHORIZED.value(), "유효하지 않은 토큰 서명 입니다");
        } catch (ExpiredJwtException e) {
            log.warn("Expired JWT token: 만료된 JWT 토큰");
            throw new CustomJwtException(HttpStatus.UNAUTHORIZED.value(), "만료된 토큰입니다");
        } catch (UnsupportedJwtException e) {
            log.warn("Unsupported JWT token: 지원되지 않는 JWT 토큰");
            throw new CustomJwtException(HttpStatus.BAD_REQUEST.value(), "지원되지 않는 토큰입니다");
        } catch (IllegalArgumentException e) {
            log.warn("JWT claims is empty: 잘못된 JWT 토큰");
            throw new CustomJwtException(HttpStatus.BAD_REQUEST.value(), "잘못된 JWT 토큰입니다");
        }
    }


}
