package group11.medScriptAPI.controller;

import group11.medScriptAPI.DTO.PatientAccountDTO;
import group11.medScriptAPI.DTO.RequestRes;
import group11.medScriptAPI.entity.*;
import group11.medScriptAPI.repository.InsuranceRepository;
import group11.medScriptAPI.service.AuthService;
import group11.medScriptAPI.util.AccountPatientValidator;
import group11.medScriptAPI.util.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Date;
/**
 * AuthController handles authentication-related endpoints for the application.
 * This includes patient registration, physician registration, and sign-in functionality.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;
    @Autowired
    private Mapper mapper;
    @Autowired
    private InsuranceRepository insuranceRepository;
    @Autowired
    private AuthService registrationService;
    @Autowired
    AccountPatientValidator accountPatientValidator;

    /**
     * Initializes the application by registering default entities (admin, physician, insurance).
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping("/startapp")
    public void gettingStarted() {
        registerInsurances();
        registerAdmin();
        registerPhysician();
    }

    /**
     * Registers a new patient.
     *
     * @param signUpRequest The request object containing patient registration details.
     * @param bindingResult Contains the results of the validation checks.
     * @return A ResponseEntity containing the result of the registration process.
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping("/signUpPatient")
    public ResponseEntity<RequestRes> signUpPatient(@RequestBody RequestRes signUpRequest,
                                                    BindingResult bindingResult) {
        //TODO check insurance number and other fields
        PatientAccountDTO patientAccountDTO = mapper.mapToPatientAccountDTO(signUpRequest);
        accountPatientValidator.validate(patientAccountDTO, bindingResult);
        if(bindingResult.hasErrors()) {
            RequestRes res = new RequestRes();

            res.setBindingResult(bindingResult.getAllErrors());
            res.setMessage("Validation Error");
            res.setStatusCode(422);
            return ResponseEntity.status(422).body(res);
        }
        Account account = mapper.mapToAccount(signUpRequest);
        Patient patient = mapper.mapToPatient(signUpRequest);

        return ResponseEntity.ok(authService.registerPatient(account, patient));
    }
    /**
     * Authenticates a user (patient or physician) by signing in.
     *
     * @param signinRequest The request object containing the sign-in credentials.
     * @return A ResponseEntity containing the result of the sign-in process.
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping("/signin")
    public ResponseEntity<RequestRes> signIn(@RequestBody RequestRes signinRequest) {


        return ResponseEntity.ok(authService.signIn(signinRequest));
    }
    /**
     * Refreshes the authentication token for a user.
     *
     * @param refreshTokenRequest The request object containing the refresh token.
     * @return A ResponseEntity containing the result of the token refresh process.
     */
    @PostMapping("/refresh")
    public ResponseEntity<RequestRes> refreshToken(@RequestBody RequestRes refreshTokenRequest) {

        return ResponseEntity.ok(authService.refreshToken(refreshTokenRequest));
    }


    /**
     * Registers a set of default insurance entities.
     */
    public void registerInsurances() {
        for(int i = 0; i < 100; i++) {
            Insurance insurance = new Insurance();
            insurance.setNumber("10000" + i);
            insurance.setInsurance_company("AOK");
            insuranceRepository.save(insurance);
        }
    }
    /**
     * Registers a default physician in the system along with the necessary account details.
     */
    public void registerPhysician() {

        Institution institution = new Institution("institution2", "NYC", "USA");
        Account account = new Account();
        Admin admin = new Admin();
        Physician physician = new Physician();
        Account adminAcc = new Account();

        admin.setFirstname("admin1");
        admin.setSecondName("admin2");
        admin.setAddress(" ");
        admin.setBirthDate(Date.from(Instant.now()));

        adminAcc.setRole("ROLE_ADMIN");
        adminAcc.setUsername("admin6");
        adminAcc.setPassword("admin6");
        adminAcc.setEmail("admin6@gmail.com");
        adminAcc.setPhoneNumber("4912313322941");
        adminAcc.setCreationDate(Date.from(Instant.now()));
        adminAcc.setUnblocked(true);



        physician.setAddress(" ");
        physician.setFirstname("fs");
        physician.setSecondName("sn");
        physician.setBirthDate(Date.from(Instant.now()));

        admin.setFirstname("admin6");
        admin.setSecondName("admin6");
        admin.setAddress(" ");
        admin.setBirthDate(Date.from(Instant.now()));
        account.setUnblocked(true);
        account.setRole("ROLE_PHYSICIAN");
        account.setUsername("ph6");
        account.setPassword("ph6");
        account.setEmail("ph6@gmail.com");
        account.setPhoneNumber("+4912313322940 ");
        account.setCreationDate(Date.from(Instant.now()));
        registrationService.registerAdmin(adminAcc, admin, admin, institution);
        registrationService.registerPhysician(account, physician, admin, institution);
    }
    /**
     * Registers a default admin in the system with the necessary account details.
     */
    public void registerAdmin() {
        Institution institution = new Institution("institution1", "NYC", "USA");
        Account account = new Account();
        Admin admin = new Admin();
        admin.setFirstname("admin1");
        admin.setSecondName("admin2");
        admin.setAddress(" ");
        admin.setBirthDate(Date.from(Instant.now()));

        account.setRole("ROLE_ADMIN");
        account.setUsername("admin1");
        account.setPassword("admin1");
        account.setEmail("admin@gmail.com");
        account.setPhoneNumber(" ");

        account.setCreationDate(Date.from(Instant.now()));
        registrationService.registerAdmin(account, admin, admin, institution);
    }



}