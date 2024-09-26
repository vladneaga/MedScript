package group11.medScriptAPI.util;



import group11.medScriptAPI.DTO.PatientAccountDTO;
import group11.medScriptAPI.entity.Insurance;
import group11.medScriptAPI.repository.InsuranceRepository;
import group11.medScriptAPI.repository.PatientRepository;
import group11.medScriptAPI.service.AccountDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;

/**
 * Validator class for validating patient account data.
 * This class ensures that the patient account information meets the specified criteria before processing.
 */
@Component
public class AccountPatientValidator implements Validator {
    private final InsuranceRepository insuranceRepository;
    private final AccountValidator accountValidator;
    private final AccountDetailsService accountDetailsService;
    private final PatientRepository patientRepository;

    /**
     * Constructor for AccountPatientValidator that initializes the repositories and services.
     *
     * @param insuranceRepository    the repository for insurance information
     * @param accountValidator       the account validator for checking account-related fields
     * @param accountDetailsService  the service for account details operations
     * @param patientRepository      the repository for patient information
     */
    @Autowired
    public AccountPatientValidator(InsuranceRepository insuranceRepository, AccountValidator accountValidator, AccountDetailsService accountDetailsService, PatientRepository patientRepository) {
        this.insuranceRepository = insuranceRepository;
        this.accountValidator = accountValidator;
        this.accountDetailsService = accountDetailsService;
        this.patientRepository = patientRepository;
    }
    /**
     * Indicates whether this validator supports the given class type.
     *
     * @param clazz the class type to check for support
     * @return true if the class type is supported; otherwise, false
     */
    @Override
    public boolean supports(Class<?> clazz) {
        return PatientAccountDTO.class.equals(clazz);
    }
    /**
     * Validates the provided patient account data against specified rules.
     *
     * @param target the object to validate, expected to be of type PatientAccountDTO
     * @param errors the object to hold validation errors
     */
    @Override
    public void validate(Object target, Errors errors) {
        PatientAccountDTO patientAccount = (PatientAccountDTO) target;

        checkPatient(errors, patientAccount);
        accountValidator.checkAccount(errors, patientAccount.getAccount());
    }

    /**
     * Checks the validity of the patient's data.
     *
     * @param errors the object to hold validation errors
     * @param patient the PatientAccountDTO object to validate
     */
    public void checkPatient(Errors errors, PatientAccountDTO patient) {
       this.checkPatient(errors, patient, true);
    }
    /**
     * Checks the validity of the patient's data with an option to check insurance.
     *
     * @param errors           the object to hold validation errors
     * @param patient          the PatientAccountDTO object to validate
     * @param checkInsurance   flag indicating whether to check insurance details
     */
    public void checkPatient(Errors errors, PatientAccountDTO patient, boolean checkInsurance) {
        if(checkInsurance) {
            Optional<Insurance> insuranceOptional = insuranceRepository.findByNumber(patient.getInsuranceNumber()).stream().findAny();
            if(insuranceOptional.isEmpty()) {
                errors.rejectValue("insuranceNumber", "", "Insurance number does not exist!");
            }


            if(!patientRepository.findAll().stream().filter(patient1 ->{
                if(patient1.getInsurance() == null) {
                    return false;
                }
                return patient1.getInsurance().getNumber().equals(patient.getInsuranceNumber());}).toList().isEmpty()) {
                errors.rejectValue("insuranceNumber", "", "The patient with this insurance number is already registered!");
            }
        }



        if(patient.getFirstname() == null) {
            errors.rejectValue("firstname", "", "The first name must not be empty!");
        } else  if(patient.getFirstname().trim().equals("")) {
            errors.rejectValue("firstname", "", "The first name must not be empty!");
        }

        if(patient.getSecondName() == null) {
            errors.rejectValue("secondname", "", "The second name must not be empty!"); //was secondName
        } else  if(patient.getSecondName().trim().equals("") ) {
            errors.rejectValue("secondname", "", "The second name must not be empty!"); //was secondName
        }

        if(patient.getAddress() == null) {
            errors.rejectValue("address", "", "The address must not be empty!");
        } else if(patient.getAddress().trim().equals("")) {
            errors.rejectValue("address", "", "The address must not be empty!");
        }



        if(patient.getBirthDate() == null) {
            errors.rejectValue("birthDate", "", "The birth date must not be null!");
        }
        if(patient.getBirthDate() != null && patient.getBirthDate().after(Date.from(Instant.now())) ) {
            errors.rejectValue("birthDate", "", "The birth date must not be from future!");
        }
      /*  Date date18YearsAgo = Date.from(Instant.now());
        date18YearsAgo.setYear(date18YearsAgo.getYear() - 18);
        //System.out.println(date18YearsAgo.toString());
        if(patient.getBirthDate() != null && !patient.getBirthDate().before(date18YearsAgo)) {
            errors.rejectValue("birthDate", "", "The patient must be 18 years old!");
        }*/
    }
}
