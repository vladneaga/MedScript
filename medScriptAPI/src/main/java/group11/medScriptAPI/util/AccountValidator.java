package group11.medScriptAPI.util;


import group11.medScriptAPI.entity.Account;
import group11.medScriptAPI.service.AccountDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Validator class for validating account data.
 * This class ensures that account information meets the specified criteria before processing.
 */
@Component
public class AccountValidator implements Validator {
private final AccountDetailsService accountDetailsService;

    /**
     * Constructor for AccountValidator that initializes the account details service.
     *
     * @param personDetailsService the service for account-related operations
     */
@Autowired
    public AccountValidator(AccountDetailsService personDetailsService) {
        this.accountDetailsService = personDetailsService;
    }
    /**
     * Indicates whether this validator supports the given class type.
     *
     * @param clazz the class type to check for support
     * @return true if the class type is supported; otherwise, false
     */
    @Override
    public boolean supports(Class<?> clazz) {
        return Account.class.equals(clazz);
    }
    /**
     * Validates the provided account data against specified rules.
     *
     * @param target the object to validate, expected to be of type Account
     * @param errors the object to hold validation errors
     */
    @Override
    public void validate(Object target, Errors errors) {
        Account person = (Account)target;
        checkAccount(errors, person);
    }
    /**
     * Checks the validity of the account data.
     *
     * @param errors the object to hold validation errors
     * @param account the Account object to validate
     */
    public void checkAccount(Errors errors, Account account) {

        /* 1 */
        if(account.getPassword()==null || Objects.equals(account.getPassword(), ""))
            errors.rejectValue("password", "", "Password must not be empty!");
        /* 2 */
        Pattern pattern = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");
        Matcher mat = pattern.matcher(account.getEmail());
        if(!mat.matches()){
            errors.rejectValue("email", "", "The inputted email is not valid.");
        }
        ////////////////////////////////////////////////////////
        if(accountDetailsService.findUserByEmail(account.getEmail()) != null)
            errors.rejectValue("email", "", "User with the same email already exists");

        /*3 */

        //System.out.println(person.getPhoneNumber());
        if(!account.getPhoneNumber().startsWith("+49") || (account.getPhoneNumber().length() >16)
                || (account.getPhoneNumber().length() < 13)){
            errors.rejectValue("phoneNumber", "", "Please input a valid German mobile number starting with +49 and should be valid.");
        }
        ////////////////////////////////////////////////////////////
        if(accountDetailsService.findUserByPhoneNumber(account.getPhoneNumber()) != null)
            errors.rejectValue("phoneNumber", "", "User with the same phone number already exists");


        if(account.getUsername().trim().equals("") || account.getUsername() == null) {
            errors.rejectValue("username", "", "Username must not be empty");
            return;
        }

        //if a user exists and then we do not register him
        /*4 */
        if(account.getUsername().contains("@") || account.getUsername().contains(" "))
            errors.rejectValue("username", "", "Username should not contain @ or whitespaces!");
        //////////////////////////////////////
        String loginRegex = ".*[a-zA-Z].*";
        Pattern usernamePattern = Pattern.compile(loginRegex);
        Matcher matcher = usernamePattern.matcher(account.getUsername());
        if(!matcher.matches())
            errors.rejectValue("username", "", "Username should contain at least one letter!");
        ////////////////////////////////////
        try
        {
            accountDetailsService.loadUserByUsername(account.getUsername());
        }catch (UsernameNotFoundException exception)
        //if the user does not exist already, the exception is thrown and we register him
        {
            return;
        }
        errors.rejectValue("username", "", "User with the same nickname already exists");
    }

}
