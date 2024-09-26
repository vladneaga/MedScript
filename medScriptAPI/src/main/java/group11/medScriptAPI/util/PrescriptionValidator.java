package group11.medScriptAPI.util;


import group11.medScriptAPI.entity.Prescription;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.sql.Date;
import java.time.Instant;

/**
 * Validator for prescription objects.
 * This class implements the Spring Validator interface to validate
 * the properties of a Prescription object before it is processed.
 */
@Component
public class PrescriptionValidator implements Validator {
    /**
     * Indicates whether this validator can validate instances of the specified class.
     *
     * @param clazz the class to check for support
     * @return true if the class is Prescription, false otherwise
     */
    @Override
    public boolean supports(Class<?> clazz) {
        return Prescription.class.equals(clazz);
    }
    /**
     * Validates the given target object, adding any validation errors to the provided Errors object.
     *
     * @param target the object to validate (expected to be a Prescription)
     * @param errors an object to hold any validation errors
     */
    @Override
    public void validate(Object target, Errors errors) {
        Prescription prescription = (Prescription) target;
        if(prescription.getExpirationDate() == null ) {
            errors.rejectValue("expirationDate", "", "Please specify the expiration date!");
        }
        if(prescription.getExpirationDate() != null &&prescription.getExpirationDate().before(Date.from(Instant.now()))) {
            errors.rejectValue("expirationDate", "", "Expiration Date cannot be earlier than today");
        }
        if(prescription.getMedication() == null || prescription.getMedication().trim().equals("")) {
            errors.rejectValue("medication", "", "Please specify the medication!");
        }
    }
}
