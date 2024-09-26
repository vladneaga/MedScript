package group11.medScriptAPI.util;

import group11.medScriptAPI.DTO.PatientAccountDTO;
import group11.medScriptAPI.DTO.PhysicianAccountDTO;
import group11.medScriptAPI.entity.Account;
import group11.medScriptAPI.entity.Patient;
import group11.medScriptAPI.service.AccountDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Validator class for editing patient account data.
 * This class ensures that patient information meets the specified criteria before updating.
 */
@Component
public class EditPatientValidator implements Validator {
    private final AccountPatientValidator accountPatientValidator;
    private final AccountDetailsService accountDetailsService;
    private Patient patientToUpdate;

    /**
     * Constructor for EditPatientValidator that initializes the required validators and services.
     *
     * @param accountPatientValidator the validator for patient account data
     * @param accountDetailsService   the service for account-related operations
     */
    @Autowired
    public EditPatientValidator(AccountPatientValidator accountPatientValidator, AccountDetailsService accountDetailsService) {
        this.accountPatientValidator = accountPatientValidator;
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
     * Validates the provided patient account data for editing.
     * This method checks the patient data and the associated account details.
     *
     * @param target the object to validate, expected to be of type PatientAccountDTO
     * @param errors the object to hold validation errors
     */
    //Please do not forget to set the physicianToUpdate object before starting the validating process
    @Override
    public void validate(Object target, Errors errors) {
        PatientAccountDTO patientAccountDTO = (PatientAccountDTO) target;
        // Validate patient data without checking insurance
        accountPatientValidator.checkPatient(errors, patientAccountDTO, false);
        // Validate account data
        checkAccount(errors, patientAccountDTO.getAccount(), patientToUpdate);
        // Clear the patientToUpdate reference after validation
        this.setPatientToUpdate(null);
    }
    /**
     * Sets the patient to update. This should be called before validation.
     *
     * @param patient the patient to be updated
     */
    public void setPatientToUpdate(Patient patient) {
        this.patientToUpdate = patient;
    }
    /**
     * Checks the validity of the account data during patient editing.
     *
     * @param errors the object to hold validation errors
     * @param account the Account object to validate
     * @param patientToUpdate the existing Patient object being updated
     */
    public void checkAccount(Errors errors, Account account, Patient patientToUpdate) {
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
                if (!foundAccount.getEmail().equals(patientToUpdate.getAccount().getEmail())) {
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
                if(!foundAccount.getPhoneNumber().equals(patientToUpdate.getAccount().getPhoneNumber())) {
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
        if(!account.getUsername().equals(patientToUpdate.getAccount().getUsername())) {
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
