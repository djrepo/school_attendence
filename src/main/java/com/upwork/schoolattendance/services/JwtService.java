package com.upwork.schoolattendance.services;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Service
public class JwtService  {

    public JwtService(@Value("${jwt.base64-min256bit-secret}") String base64Min256bitSecret,@Value("${jwt.token.lifespan}") int tokenLifespan) {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(base64Min256bitSecret));
        this.tokenLifeSpan = tokenLifespan;
    }

    private final Key secretKey;
    private final int tokenLifeSpan;

    
    public String extractUserId(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(this.secretKey).build().parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (ExpiredJwtException e){
            return true;
        }
    }

    
    public String generateToken(long userId, Map<String, Object> claims) {
        return createToken(claims, String.valueOf(userId));
    }



    private String createToken(Map<String, Object> claims, String userId) {
        return createToken(this.secretKey, tokenLifeSpan, claims, userId);
    }

    public static String createToken(Key secretKey, int expirationInSecondsFromNow, Map<String, Object> claims, String userId) {
        return Jwts.builder().setClaims(claims).setSubject(userId).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(expirationInSecondsFromNow)))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compressWith(CompressionCodecs.GZIP)
                .compact();
    }
    
    public Boolean validateToken(String token, long userId) {
        if (StringUtils.isEmpty(token)) {
            return false;
        }
        if (isTokenExpired(token)) {
            return false;
        }
        final String tokenSubject = extractUserId(token);
        return !StringUtils.isEmpty(tokenSubject) && StringUtils.isNumeric(tokenSubject) && Long.valueOf(tokenSubject).equals(userId);
    }

     /*
    *
        SecretKey secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(base64Min256bitSecret);

        Jwts.builder().setClaims(Collections.singletonMap(CHECKIN_PAYLOAD, checkInPayload)).setSubject(1L).setIssuedAt(new Date(System.currentTimeMillis()))
                        .setExpiration(new Date(System.currentTimeMillis() - 1))
                        .signWith(secretKey, SignatureAlgorithm.HS256)
                        .compressWith(CompressionCodecs.GZIP)
                        .compact();
        checkInInput.setToken();
    * */
}

