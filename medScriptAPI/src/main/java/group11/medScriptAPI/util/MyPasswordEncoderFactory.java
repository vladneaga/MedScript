package group11.medScriptAPI.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Factory class for creating instances of BCryptPasswordEncoder.
 * This class provides a singleton instance of BCryptPasswordEncoder
 * which can be used for password encoding and validation.
 */
@Component
public class MyPasswordEncoderFactory {
    // Singleton instance of BCryptPasswordEncoder
   private  final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * Returns the singleton instance of BCryptPasswordEncoder.
     *
     * @return a BCryptPasswordEncoder instance
     */
   public  BCryptPasswordEncoder getInstance() {
        return passwordEncoder;
    }
}
