package group11.medScriptAPI.util;


import group11.medScriptAPI.DTO.PhysicianAccountDTO;
import group11.medScriptAPI.entity.Physician;
import group11.medScriptAPI.service.AccountDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.Instant;
import java.util.Date;

/**
 * Validator class for validating physician account data.
 * This class ensures that the physician account information meets the specified criteria before processing.
 */
@Component
public class AccountPhysicianValidator implements Validator {
    private final AccountValidator accountValidator;
    private final AccountDetailsService accountDetailsService;

    /**
     * Constructor for AccountPhysicianValidator that initializes the necessary validators and services.
     *
     * @param accountValidator       the account validator for checking account-related fields
     * @param accountDetailsService  the service for account details operations
     */
    @Autowired
    public AccountPhysicianValidator(AccountValidator accountValidator, AccountDetailsService accountDetailsService) {
        this.accountValidator = accountValidator;
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
     * Validates the provided physician account data against specified rules.
     *
     * @param target the object to validate, expected to be of type PhysicianAccountDTO
     * @param errors the object to hold validation errors
     */
    @Override
    public void validate(Object target, Errors errors) {

        PhysicianAccountDTO physicianAccount = (PhysicianAccountDTO) target;

        checkPhysician(errors, physicianAccount.getPhysician());

        accountValidator.checkAccount(errors, physicianAccount.getAccount());

    }

    /**
     * Checks the validity of the physician's data.
     *
     * @param errors    the object to hold validation errors
     * @param physician the Physician object to validate
     */
    public void checkPhysician(Errors errors, Physician physician) {

        if(physician.getFirstname() == null || physician.getFirstname().trim().equals("")  ) {
            errors.rejectValue("firstname", "", "The first name must not be empty!");
        }
        if(physician.getSecondName() == null || physician.getSecondName().trim().equals("") ) {
            errors.rejectValue("secondname", "", "The second name must not be empty!");
        }
        if(physician.getAddress() == null || physician.getAddress().trim().equals("") ) {
            errors.rejectValue("address", "", "The address must not be empty!");
        }
        if(physician.getBirthDate() == null) {
            errors.rejectValue("birthDate", "", "The birth date must not be null!");
        }
        if(physician.getBirthDate() != null && physician.getBirthDate().after(Date.from(Instant.now()))) {
            errors.rejectValue("birthDate", "", "The birth date must not be from future!");
        }
        Date date18YearsAgo = Date.from(Instant.now());
        date18YearsAgo.setYear(date18YearsAgo.getYear() - 18);
        System.out.println(date18YearsAgo.toString());
        if(physician.getBirthDate() != null) {
            if (!physician.getBirthDate().before(date18YearsAgo)) {
                errors.rejectValue("birthDate", "", "The physician must be 18 years old!");
            }
        } else {
            errors.rejectValue("birthDate", "", "The birth date must not be empty!");
        }
    }
}
