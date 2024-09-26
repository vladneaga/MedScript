package group11.medScriptAPI.repository;

import group11.medScriptAPI.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for {@link Account} entities.
 * Provides methods to find accounts by username, email, or phone number.
 */
@Repository
@Component
public interface AccountRepository extends JpaRepository<Account, Long> {

    /**
     * Finds an account by username.
     *
     * @param username the username to search for.
     * @return an Optional containing the account if found.
     */
    Optional<Account> findByUsername(String username);

    /**
     * Finds an account by email.
     *
     * @param email the email to search for.
     * @return an Optional containing the account if found.
     */
    Optional<Account> findByEmail(String email);

    /**
     * Finds an account by phone number.
     *
     * @param phoneNumber the phone number to search for.
     * @return an Optional containing the account if found.
     */
    Optional<Account> findByPhoneNumber(String phoneNumber);
}
