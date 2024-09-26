package group11.medScriptAPI.DTO;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import group11.medScriptAPI.entity.*;
import lombok.Data;

import org.springframework.validation.ObjectError;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL) //maybe not
public class RequestRes {

    private int statusCode;
    private String error;
    private String message;
    private String token;
    private String refreshToken;
    private String expirationTime;
    private String name;
    private String email;
    private String username;
    private String phoneNumber;
    private String role;
    private String password;
    private Account account;
    private String insuranceNumber;
    private String firstname;
   private String secondname;
    private String address;
    private Date birthDate;
    private Long institutionId;
    private Long creatorAdminId;
    PrescriptionDetailsDTO prescriptionDetailsDTO;
    PatientDetailsDTO patientDetailsDTO;
    List<Prescription> pagedAndSortedPatientPrescriptions;
    Admin admin;
    private Boolean isUnblocked;
    private Physician physician;
    List<Patient> assignedPatients;
    List<Patient> allPatients;
    List<Physician> allPhysicians;
    private Patient patient;
    private List<PrescriptionDetailsDTO> prescriptionDetailsDTOList;
    private String text;
    private String Medication;
    private String totalGrammage;
    private Date expirationDate;
    private Prescription prescription;
    private Person person;
    private List<Prescription> prescriptions;
    private PersonDTO personDTO;
    private Admin creatorAdmin;
    private List<ObjectError> bindingResult;
    private MultipartFile signatureFile;
    private PhysicianDTO physicianDTO;
    private int prescriptionPagesSize;
    private PatientDTO patientDTO;
    private List<Long> physicianIds;

}
