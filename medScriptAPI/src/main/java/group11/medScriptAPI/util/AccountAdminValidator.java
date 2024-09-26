package group11.medScriptAPI.util;


import group11.medScriptAPI.DTO.AdminAccountDTO;
import group11.medScriptAPI.DTO.PhysicianAccountDTO;
import group11.medScriptAPI.entity.Admin;
import group11.medScriptAPI.service.AccountDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.Instant;
import java.util.Date;

/**
 * Validator class for validating admin account data.
 * This class ensures that the admin account information meets the specified criteria before processing.
 */
@Component
public class AccountAdminValidator implements Validator {
    private final AccountValidator accountValidator;
    private final AccountDetailsService accountDetailsService;

    /**
     * Constructor for AccountAdminValidator that initializes the account validator and account details service.
     *
     * @param accountValidator      the account validator for checking account-related fields
     * @param accountDetailsService the service for account details operations
     */
    @Autowired
    public AccountAdminValidator(AccountValidator accountValidator, AccountDetailsService accountDetailsService) {
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
     * Validates the provided admin account data against specified rules.
     *
     * @param target the object to validate, expected to be of type AdminAccountDTO
     * @param errors the object to hold validation errors
     */
    @Override
    public void validate(Object target, Errors errors) {

        AdminAccountDTO adminAccountDTO = (AdminAccountDTO) target;

        checkAdmin(errors, adminAccountDTO.getAdmin());

        accountValidator.checkAccount(errors, adminAccountDTO.getAccount());

    }

    /**
     * Checks the validity of the admin's data.
     *
     * @param errors the object to hold validation errors
     * @param admin  the admin object to validate
     */
    public void checkAdmin(Errors errors, Admin admin) {

        if(admin.getFirstname() == null || admin.getFirstname().trim().equals("")  ) {
            errors.rejectValue("firstname", "", "The first name must not be empty!");
        }
        if(admin.getSecondName() == null || admin.getSecondName().trim().equals("") ) {
            errors.rejectValue("secondname", "", "The second name must not be empty!");
        }
        if(admin.getAddress() == null || admin.getAddress().trim().equals("") ) {
            errors.rejectValue("address", "", "The address must not be empty!");
        }
        if(admin.getBirthDate() == null) {
            errors.rejectValue("birthDate", "", "The birth date must not be null!");
        }
        if(admin.getBirthDate() != null && admin.getBirthDate().after(Date.from(Instant.now()))) {
            errors.rejectValue("birthDate", "", "The birth date must not be from future!");
        }
        Date date18YearsAgo = Date.from(Instant.now());
        date18YearsAgo.setYear(date18YearsAgo.getYear() - 18);
        System.out.println(date18YearsAgo.toString());
        if(admin.getBirthDate() != null) {

        } else {
            errors.rejectValue("birthDate", "", "The birth date must not be empty!");
        }
    }

}

