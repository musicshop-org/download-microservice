package fhv.musicshop.domain;

import io.jsonwebtoken.*;

import java.util.Date;

public class JwtManager {

    private static final byte[] SECRET_KEY = "SECRET_OF_MUSIC-SHOP".getBytes();

    public static String getId(String jwt) {
        return getClaims(jwt).getId();
    }

    public static String getIssuer(String jwt) {
        return getClaims(jwt).getIssuer();
    }

    public static Date getIssuedAt(String jwt) {
        return getClaims(jwt).getIssuedAt();
    }

    public static String getEmailAddress(String jwt) {
        return getClaims(jwt).get("email", String.class);
    }

    public static Date getExpiration(String jwt) {
        return getClaims(jwt).getExpiration();
    }

    private static Claims getClaims(String jwt) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(jwt).getBody();
    }

    public static boolean isValidToken(String jwt) {
        try {
            getClaims(jwt);
        }
        catch (JwtException e) {
            return false;
        }

        return true;
    }
}