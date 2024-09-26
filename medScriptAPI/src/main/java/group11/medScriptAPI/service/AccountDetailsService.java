package group11.medScriptAPI.service;

import group11.medScriptAPI.entity.Account;
import group11.medScriptAPI.entity.AccountDetails;
import group11.medScriptAPI.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Optional;

/**
 * Service class that implements the UserDetailsService interface to load user-specific data
 * from the database for authentication and authorization.
 */
@Service
public class AccountDetailsService implements UserDetailsService {

    private final AccountRepository accountRepository;
@Autowired
    public AccountDetailsService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * Loads a user by their username. If no user is found, a UsernameNotFoundException is thrown.
     *
     * @param username the username of the user to be loaded
     * @return UserDetails object containing user information
     * @throws UsernameNotFoundException if the user is not found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Account> person = accountRepository.findByUsername(username);
        if(person.isEmpty())
            throw new UsernameNotFoundException("User not found!");

        return new AccountDetails(person.get());
    }
    /**
     * Finds a user by their email. Returns null if no user is found with the given email.
     *
     * @param email the email of the user to be found
     * @return the Account object if found, or null otherwise
     * @throws UsernameNotFoundException if the user is not found (not applicable in this case)
     */
    public Account findUserByEmail(String email) throws UsernameNotFoundException {
        Optional<Account> person = accountRepository.findByEmail(email);
        return person.orElse(null);

    }
    /**
     * Finds a user by their phone number. Returns null if no user is found with the given phone number.
     *
     * @param phoneNumber the phone number of the user to be found
     * @return the Account object if found, or null otherwise
     * @throws UsernameNotFoundException if the user is not found (not applicable in this case)
     */
    public Account findUserByPhoneNumber(String phoneNumber) throws UsernameNotFoundException {
        Optional<Account> person = accountRepository.findByPhoneNumber(phoneNumber);
        return person.orElse(null);


    }
}
