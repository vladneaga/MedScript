package group11.medScriptAPI.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Function;

/**
 * Utility class for generating and validating JSON Web Tokens (JWT) for user authentication.
 * This class provides methods to create access tokens and refresh tokens, as well as to validate them.
 */
@Component
public class JWTUtils {

    private SecretKey Key;
    private  static  final long EXPIRATION_TIME = 86400000; //24hours or 86400000 milisecs
    /**
     * Constructor that initializes the secret key used for signing the JWTs.
     */
    public JWTUtils(){
        String secreteString = "843567893696976453275974432697R634976R738467TR678T34865R6834R8763T478378637664538745673865783678548735687R3";
        byte[] keyBytes = Base64.getDecoder().decode(secreteString.getBytes(StandardCharsets.UTF_8));
        this.Key = new SecretKeySpec(keyBytes, "HmacSHA256");
    }

    /**
     * Generates a JWT token for the specified user details.
     *
     * @param userDetails the user details for whom the token is generated
     * @return a signed JWT token as a String
     */
    public String generateToken(UserDetails userDetails){
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(Key)
                .compact();
    }
    /**
     * Generates a refresh token with additional claims for the specified user details.
     *
     * @param claims     a map of claims to include in the refresh token
     * @param userDetails the user details for whom the refresh token is generated
     * @return a signed refresh token as a String
     */
    public String generateRefreshToken(HashMap<String, Object> claims, UserDetails userDetails){
        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(Key)
                .compact();
    }
    /**
     * Extracts the username from the provided JWT token.
     *
     * @param token the JWT token from which to extract the username
     * @return the username contained in the token
     */
    public String extractUsername(String token){
        return extractClaims(token, Claims::getSubject);
    }
    /**
     * Extracts claims from the provided JWT token using a specified function.
     *
     * @param token              the JWT token from which to extract claims
     * @param claimsTFunction    the function used to extract specific claims
     * @param <T>               the type of the claims to extract
     * @return the extracted claims of type T
     */
    private <T> T extractClaims(String token, Function<Claims, T> claimsTFunction){
        return claimsTFunction.apply(Jwts.parser().verifyWith(Key).build().parseSignedClaims(token).getPayload());
    }
    /**
     * Validates the provided JWT token against the user details.
     *
     * @param token       the JWT token to validate
     * @param userDetails the user details to compare against
     * @return true if the token is valid, false otherwise
     */
    public boolean isTokenValid(String token, UserDetails userDetails){
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
    /**
     * Checks whether the provided JWT token has expired.
     *
     * @param token the JWT token to check for expiration
     * @return true if the token is expired, false otherwise
     */
    public boolean isTokenExpired(String token){
        return extractClaims(token, Claims::getExpiration).before(new Date());
    }

}
