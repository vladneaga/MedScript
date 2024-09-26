package group11.medScriptAPI.service;

import group11.medScriptAPI.DTO.RequestRes;
import group11.medScriptAPI.entity.*;
import group11.medScriptAPI.repository.*;
import group11.medScriptAPI.security.AuthProviderImpl;
import group11.medScriptAPI.util.MyPasswordEncoderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;

/**
 * Service class responsible for handling authentication and authorization operations
 * including user registration, login, and token management.
 */
@Service
public class AuthService {

    private final JWTUtils jwtUtils;
    private final PatientRepository patientRepository;
    private final AdminRepository adminRepository;
    private final MyPasswordEncoderFactory myPasswordEncoderFactory;
    private final PhysicianRepository physicianRepository;
    private final AccountDetailsService accountDetailsService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    private final AccountRepository accountRepository;
    private final InstitutionRepository institutionRepository;
    private  final InsuranceRepository insuranceRepository;
    private final AuthProviderImpl authProvider;

@Autowired
    public AuthService(JWTUtils jwtUtils, PatientRepository patientRepository, AdminRepository adminRepository, MyPasswordEncoderFactory myPasswordEncoderFactory, PhysicianRepository physicianRepository, AccountDetailsService accountDetailsService, AccountRepository accountRepository, InstitutionRepository institutionRepository, InsuranceRepository insuranceRepository, AuthProviderImpl authProvider) {
        this.jwtUtils = jwtUtils;
        this.patientRepository = patientRepository;
        this.adminRepository = adminRepository;
        this.myPasswordEncoderFactory = myPasswordEncoderFactory;
        this.physicianRepository = physicianRepository;
        this.accountDetailsService = accountDetailsService;
        this.accountRepository = accountRepository;
        this.institutionRepository = institutionRepository;
        this.insuranceRepository = insuranceRepository;
        this.authProvider = authProvider;
}
    /**
     * Registers a new physician account and saves associated entities.
     *
     * @param user          the account information
     * @param physician     the physician details
     * @param creatorAdmin  the admin who created the physician
     * @param institution   the institution the physician belongs to
     * @return RequestRes object containing the status and result of the operation
     */
    @Transactional
    public RequestRes registerPhysician(Account user, Physician physician, Admin creatorAdmin, Institution institution) {
        RequestRes resp = new RequestRes();
        try {
            this.institutionRepository.save(institution);
            String encodedPassword = myPasswordEncoderFactory.getInstance().encode(user.getPassword());
            user.setPassword(encodedPassword);
            user.setRole("ROLE_PHYSICIAN");
            user.setPerson(physician); //attaching the person subtype to the account
            physician.setCreatorAdmin(creatorAdmin);
            physician.setInstitution(institution);
            physicianRepository.save(physician);
            Account accountResult = accountRepository.save(user);
            if (accountResult != null && accountResult.getId()>0) {
                resp.setAccount(accountResult);
                resp.setMessage("Account Saved Successfully");
                resp.setStatusCode(200);
            }
        }  catch (Exception e) {
        resp.setStatusCode(500);
        resp.setError(e.getMessage());
    }
    return resp;
    }
    /**
     * Edits an existing physician account, optionally updating the password.
     *
     * @param account       the account information to be edited
     * @param physician     the physician details to be updated
     * @param samePassword  indicates whether the password remains unchanged
     */
    @Transactional
   public void editPhysician(Account account, Physician physician, boolean samePassword) {
    if(!samePassword) {
        String encodedPassword = myPasswordEncoderFactory.getInstance().encode(account.getPassword());
        account.setPassword(encodedPassword);
    }
        physicianRepository.save(physician);
        accountRepository.save(account);
    }

    /**
     * Edits an existing patient account, optionally updating the password.
     *
     * @param account       the account information to be edited
     * @param patient       the patient details to be updated
     * @param samePassword  indicates whether the password remains unchanged
     */
    @Transactional
    public void editPatient(Account account, Patient patient, boolean samePassword) {
        if(!samePassword) {
            String encodedPassword = myPasswordEncoderFactory.getInstance().encode(account.getPassword());
            account.setPassword(encodedPassword);
        }
        patientRepository.save(patient);
        accountRepository.save(account);
    }

    /**
     * Registers a new admin account and saves associated entities.
     *
     * @param user          the account information
     * @param admin         the admin details
     * @param creatorAdmin  the admin who created the new admin
     * @param institution   the institution the admin belongs to
     * @return RequestRes object containing the status and result of the operation
     */
    @Transactional
    public RequestRes registerAdmin(Account user, Admin admin, Admin creatorAdmin, Institution institution) {
        RequestRes resp = new RequestRes();


        try {
            String encodedPassword = myPasswordEncoderFactory.getInstance().encode(user.getPassword());

            this.institutionRepository.save(institution);

            user.setPassword(encodedPassword);
            user.setRole("ROLE_ADMIN");
            user.setPerson(admin); //user
            user.setUnblocked(true);
            admin.setCreatorAdmin(creatorAdmin);
            admin.setInstitution(institution);
            adminRepository.save(admin);
            Account accountResult = accountRepository.save(user);
            if (accountResult != null && accountResult.getId()>0) {
                resp.setAccount(accountResult);
                resp.setMessage("Account Saved Successfully");
                resp.setStatusCode(200);
            }
        }catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }

      return resp;
    }
    /**
     * Registers a new patient account and saves associated entities.
     *
     * @param user      the account information
     * @param person    the patient details
     * @return RequestRes object containing the status and result of the operation
     */
    @Transactional
    public RequestRes registerPatient(Account user, Patient person) {
        RequestRes resp = new RequestRes();
        try {
            String encodedPassword = myPasswordEncoderFactory.getInstance().encode(user.getPassword());
            user.setPassword(encodedPassword);
            user.setRole("ROLE_PATIENT");
            user.setPerson(person);
            user.setUnblocked(true);
            user.setCreationDate(Date.from(Instant.now()));
            System.out.println("Insurance number" + person.getInsurance().getNumber());
            person.getInsurance().setPatient(person);

            patientRepository.save(person);
            insuranceRepository.save(person.getInsurance());
           Account accountResult = accountRepository.save(user);

            if (accountResult != null && accountResult.getId()>0) {
                resp.setAccount(accountResult);
                resp.setMessage("Account Saved Successfully");
                resp.setStatusCode(200);
            }
        }catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }

        return resp;

    }
    /**
     * Signs up a new user based on registration details provided in the request.
     *
     * @param registrationRequest the request containing user details for registration
     * @return RequestRes object containing the status and result of the operation
     */
    public RequestRes signUp(RequestRes registrationRequest)    {
        RequestRes resp = new RequestRes();
        try {
            //AccountDetails ourUsers = new AccountDetails();
            Account account = new Account();
            account.setUsername(registrationRequest.getUsername());
            account.setPhoneNumber(registrationRequest.getPhoneNumber());
            account.setEmail(registrationRequest.getEmail());
            account.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
            account.setRole(registrationRequest.getRole());
            account.setCreationDate(Date.from(Instant.now()));


            Patient patient = new Patient();
            patient.setAddress("");
            patient.setBirthDate(new Date());
            patient.setFirstname("");
            patient.setSecondName("");
            patient.setAccount(account);
            //patient.setInsurance(insurance);
            //insuranceRepository.save(insurance);
            patient.setAccount(account);
            account.setPerson(patient);
            Account accountResult = accountRepository.save(account);
            patientRepository.save(patient);

            if (accountResult != null && accountResult.getId()>0) {
                resp.setAccount(accountResult);
                resp.setMessage("User Saved Successfully");
                resp.setStatusCode(200);
            }
        }catch (Exception e){
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;
    }

    /**
     * Authenticates a user and generates a JWT token upon successful login.
     *
     * @param signinRequest the request containing login details
     * @return RequestRes object containing the status, JWT token, and user information
     */
    public RequestRes signIn(RequestRes signinRequest){
        RequestRes response = new RequestRes();

        try {

             var authentication = authProvider.authenticate(new UsernamePasswordAuthenticationToken(signinRequest.getUsername(),signinRequest.getPassword()));
            AccountDetails accountDetails = (AccountDetails) accountDetailsService.loadUserByUsername(authentication.getName());
            System.out.println(accountDetails.getPassword()+ accountDetails.getUsername() + accountDetails.getAuthorities());
            System.out.println(authentication.getCredentials());
            System.out.println("here");
            //authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signinRequest.getUsername(),signinRequest.getPassword()));
            //var user = accountDetailsService.loadUserByUsername(signinRequest.getUsername()); //orelseTHROW
            System.out.println("USER IS: "+ accountDetails);
            var jwt = jwtUtils.generateToken(accountDetails);
            var refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), accountDetails);
            response.setStatusCode(200);
            response.setToken(jwt);
            response.setRefreshToken(refreshToken);
            response.setExpirationTime("24Hr");
            response.setMessage("Successfully Signed In");
            response.setAccount(accountDetails.getAccount());
            response.setPerson(accountDetails.getAccount().getPerson());
        }catch (Exception e){
            response.setStatusCode(500);
            response.setError(e.getMessage());
        }
        return response;
    }
    /**
     * Refreshes the JWT token using the provided refresh token.
     *
     * @param refreshTokenRequest the request containing the refresh token
     * @return RequestRes object containing the status and new JWT token
     */
    public RequestRes refreshToken(RequestRes refreshTokenRequest){
        RequestRes response = new RequestRes();
        String ourUsername = jwtUtils.extractUsername(refreshTokenRequest.getToken());
        UserDetails users = accountDetailsService.loadUserByUsername(ourUsername); //
        if (jwtUtils.isTokenValid(refreshTokenRequest.getToken(), users)) {
            var jwt = jwtUtils.generateToken(users);
            response.setStatusCode(200);
            response.setToken(jwt);
            response.setRefreshToken(refreshTokenRequest.getToken());
            response.setExpirationTime("24Hr");
            response.setMessage("Successfully Refreshed Token");
        }
        response.setStatusCode(500);
        return response;
    }
}
