package group11.medScriptAPI.util;

import group11.medScriptAPI.DTO.PhysicianAccountDTO;
import group11.medScriptAPI.entity.Account;
import group11.medScriptAPI.entity.Physician;
import group11.medScriptAPI.service.AccountDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Validator class for editing physician account data.
 * This class ensures that physician information meets the specified criteria before updating.
 */
@Component
public class EditPhysicianValidator implements Validator {
       private final AccountPhysicianValidator accountPhysicianValidator;
       private final AccountDetailsService accountDetailsService;
       private Physician physicianToUpdate;


    /**
     * Constructor for EditPhysicianValidator that initializes the required validators and services.
     *
     * @param accountPhysicianValidator the validator for physician account data
     * @param accountDetailsService     the service for account-related operations
     */
@Autowired
    public EditPhysicianValidator(AccountPhysicianValidator accountPhysicianValidator, AccountDetailsService accountDetailsService) {
        this.accountPhysicianValidator = accountPhysicianValidator;
    this.accountDetailsService = accountDetailsService;
}
    /**
     * Indicates whether this validator supports the given class type.
     *
     * @param clazz the class type to check for support
     * @return true if the class type is supported; otherwise, false
     */
    @Override
    public boolean supports(Class<?> clazz) {
         return PhysicianAccountDTO.class.equals(clazz);
    }
    /**
     * Validates the provided physician account data for editing.
     * This method checks the physician data and the associated account details.
     *
     * @param target the object to validate, expected to be of type PhysicianAccountDTO
     * @param errors the object to hold validation errors
     */
//Please do not forget to set the physicianToUpdate object before validating process
    @Override
    public void validate(Object target, Errors errors) {
        // Validate physician data
        PhysicianAccountDTO physicianAccount = (PhysicianAccountDTO) target;
        // Validate physician  data
        accountPhysicianValidator.checkPhysician(errors, physicianAccount.getPhysician());
        // Validate account data
        checkAccount(errors, physicianAccount.getAccount(), physicianToUpdate);
        // Clear the physicianToUpdate reference after validation
        this.setPhysicianToUpdate(null);
    }
    /**
     * Sets the physician to update. This should be called before validation.
     *
     * @param physician the physician to be updated
     */
    public void setPhysicianToUpdate(Physician physician) {
    this.physicianToUpdate = physician;
    }
    /**
     * Checks the validity of the account data during physician editing.
     *
     * @param errors          the object to hold validation errors
     * @param account         the Account object to validate
     * @param physicianToUpdate the existing Physician object being updated
     */
    public void checkAccount(Errors errors, Account account, Physician physicianToUpdate) {
        Account foundAccount;
        /* 1 */
        if (account.getEmail() != null) {

        Pattern pattern = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");
        Matcher mat = pattern.matcher(account.getEmail());
        if (!mat.matches()) {
            errors.rejectValue("email", "", "The inputted email is not valid.");
        }

        ////////////////////////////////////////////////////////
         foundAccount = accountDetailsService.findUserByEmail(account.getEmail());

        if(foundAccount != null) {
            if (!foundAccount.getEmail().equals(physicianToUpdate.getAccount().getEmail())) {
                errors.rejectValue("email", "", "User with the same email already exists");
            }
        }
        } else {
            errors.rejectValue("email", "", "The inputted email is empty.");
        }
        /*2 */
        if(account.getPhoneNumber() != null) {


        if(!account.getPhoneNumber().startsWith("+49") || (account.getPhoneNumber().length() >16)
                || (account.getPhoneNumber().length() < 13)){
            errors.rejectValue("phoneNumber", "", "Please input a valid German mobile number starting with +49 and should be valid.");
        }
        ////////////////////////////////////////////////////////////
        foundAccount = accountDetailsService.findUserByPhoneNumber(account.getPhoneNumber());
        if(foundAccount != null) {
            if(!foundAccount.getPhoneNumber().equals(physicianToUpdate.getAccount().getPhoneNumber())) {
                errors.rejectValue("phoneNumber", "", "User with the same phone number already exists");
            }
        }

        } else {
            errors.rejectValue("phoneNumber", "", "The phone number must not be empty!");
        }

        //if a user exists and then we do not register him
        /*3 */
        if(account.getUsername().trim().equals("") || account.getUsername() == null) {
            errors.rejectValue("username", "", "Username must not be empty");
            return;
        }
        if(account.getUsername().contains("@") || account.getUsername().contains(" "))
            errors.rejectValue("username", "", "Username should not contain @ or whitespaces!");
        //////////////////////////////////////

        String loginRegex = ".*[a-zA-Z].*";
        Pattern usernamePattern = Pattern.compile(loginRegex);
        Matcher matcher = usernamePattern.matcher(account.getUsername());
        if(!matcher.matches())
            errors.rejectValue("username", "", "Username should contain at least one letter!");
        ////////////////////////////////////
        if(!account.getUsername().equals(physicianToUpdate.getAccount().getUsername())) {
            try {
                accountDetailsService.loadUserByUsername(account.getUsername());
            } catch (UsernameNotFoundException exception)
            //if the user does not exist already, the exception is thrown and we register him
            {
                return;
            }
            errors.rejectValue("username", "", "User with the same nickname already exists");
        }
    }

}
