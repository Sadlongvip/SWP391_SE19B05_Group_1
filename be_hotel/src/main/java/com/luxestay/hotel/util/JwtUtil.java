package com.luxestay.hotel.util;

import org.springframework.stereotype.Component;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {
    
    private static final String SECRET_KEY = "mySecretKey123456789012345678901234567890123456789012345678901234567890";
    private static final int TOKEN_VALIDITY = 3600 * 1000; // 1 hour in milliseconds
    
    public String generateToken(String username, String role, int id) {
        // Simple token generation for demo purposes
        String payload = username + "|" + role + "|" + id + "|" + (System.currentTimeMillis() + TOKEN_VALIDITY);
        return Base64.getEncoder().encodeToString(payload.getBytes());
    }
    
    public String extractUsername(String token) {
        try {
            String payload = new String(Base64.getDecoder().decode(token));
            return payload.split("\\|")[0];
        } catch (Exception e) {
            return null;
        }
    }
    
    public Date extractExpiration(String token) {
        try {
            String payload = new String(Base64.getDecoder().decode(token));
            String[] parts = payload.split("\\|");
            if (parts.length >= 4) {
                long expiration = Long.parseLong(parts[3]);
                return new Date(expiration);
            }
            return new Date(0);
        } catch (Exception e) {
            return new Date(0);
        }
    }
    
    public String extractRole(String token) {
        try {
            String payload = new String(Base64.getDecoder().decode(token));
            String[] parts = payload.split("\\|");
            if (parts.length >= 2) {
                return parts[1];
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
    
    public int extractId(String token) {
        try {
            String payload = new String(Base64.getDecoder().decode(token));
            String[] parts = payload.split("\\|");
            if (parts.length >= 3) {
                return Integer.parseInt(parts[2]);
            }
            return 0;
        } catch (Exception e) {
            return 0;
        }
    }
    
    private Boolean isTokenExpired(String token) {
        Date expiration = extractExpiration(token);
        return expiration.before(new Date());
    }
    
    public Boolean validateToken(String token, String username) {
        try {
            final String extractedUsername = extractUsername(token);
            return (extractedUsername != null && extractedUsername.equals(username) && !isTokenExpired(token));
        } catch (Exception e) {
            return false;
        }
    }
}
