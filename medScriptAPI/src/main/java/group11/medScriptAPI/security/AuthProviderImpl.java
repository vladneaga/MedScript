package group11.medScriptAPI.security;
import group11.medScriptAPI.entity.Account;
import group11.medScriptAPI.entity.AccountDetails;
import group11.medScriptAPI.service.AccountDetailsService;
import group11.medScriptAPI.util.MyPasswordEncoderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;


/**
 * Custom implementation of the AuthenticationProvider interface that handles user authentication
 * using either email or phone number. It verifies user credentials against stored account details.
 */
@Service
@Component
public class AuthProviderImpl implements AuthenticationProvider {
private final AccountDetailsService personDetailsService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public AuthProviderImpl(AccountDetailsService personDetailsService, MyPasswordEncoderFactory myPasswordEncoderFactory) {
        this.personDetailsService = personDetailsService;
    this.bCryptPasswordEncoder = myPasswordEncoderFactory.getInstance();
    }
    /**
     * Authenticates the user by checking their credentials. It allows users to log in using either
     * an email address or a phone number. The password is verified using BCrypt hashing.
     *
     * @param authentication the authentication request containing the user's credentials
     * @return a fully populated Authentication object if authentication is successful
     * @throws AuthenticationException if authentication fails, e.g., user not found or incorrect password
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String username = authentication.getName();

        if(username.contains("@") && username.contains(".")) {
            Account userByEmail = personDetailsService.findUserByEmail(username);
            if(userByEmail == null)
                throw new UsernameNotFoundException("User with this email not found!");

            username = userByEmail.getUsername();
        } else if(username.startsWith("+49")) {
            Account userByPhone = personDetailsService.findUserByPhoneNumber(username);
            if(userByPhone == null)
                throw new UsernameNotFoundException("User with this phone not found!");

            username = userByPhone.getUsername();
        }
        AccountDetails personDetails = (AccountDetails) personDetailsService.loadUserByUsername(username);
        String password = authentication.getCredentials().toString();
       if(!bCryptPasswordEncoder.matches(password, personDetails.getPassword())) {
           throw new BadCredentialsException("Incorrect password");
       }
        //TODO Deny user login with disabled accounts
       if(!personDetails.isAccountNonLocked()) {
           throw new BadCredentialsException("Account is blocked");
       }

       return new UsernamePasswordAuthenticationToken(personDetails, password, personDetails.getAuthorities());
       //list of user's rights

    }
    /**
     * Indicates whether this AuthenticationProvider supports the specified authentication class.
     *
     * @param authentication the class of the authentication object to check
     * @return true if this provider can process the authentication, false otherwise
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
