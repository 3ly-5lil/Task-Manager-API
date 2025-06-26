package com.ak.task_manger_api.auth.configs;

import com.ak.task_manger_api.auth.models.AppUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    String secretKey;

    public String generateToken(AppUser user) {
        Map<String, Object> claims = new HashMap<>();
        Date currentDate = new Date(System.currentTimeMillis());
        // millis  * 1000 = seconds * 60   = minutes * 60   = hours * 24   = days
        // so we sat the expiry date in 7 days
        Date expiryDate = new Date(currentDate.getTime() + 7 * (1000 * 60 * 60 * 24));

        System.out.println("token expiry date: " + expiryDate);

        return Jwts.builder().claims(claims).subject(user.getUsername()).issuedAt(currentDate).expiration(expiryDate).signWith(getKey()).compact();
    }

    private SecretKey getKey() {
        byte[] bytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(bytes);
    }

    public String extractUsername(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    private <T> T extractClaims(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token).getPayload();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String userName = extractUsername(token);
        return userDetails.getUsername().equals(userName) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaims(token, Claims::getExpiration);
    }
}
