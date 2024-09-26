package group11.medScriptAPI.controller;

import group11.medScriptAPI.DTO.*;
import group11.medScriptAPI.entity.*;
import group11.medScriptAPI.repository.*;
import group11.medScriptAPI.service.AccountDetailsService;
import group11.medScriptAPI.service.AuthService;
import group11.medScriptAPI.util.*;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.io.File;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class AdminController {
    AccountRepository accountRepository;
    AccountDetailsService userDetailsService;
    AuthService registrationService;
    private  final AccountPhysicianValidator accountPhysicianValidator;
    private final Mapper mapper;
    private final AccountValidator accountValidator;
    private final PhysicianRepository physicianRepository;
    private final PatientRepository patientRepository;
    private final AccountAdminValidator accountAdminValidator;
    private final PrescriptionRepository prescriptionRepository;
    private final InsuranceRepository insuranceRepository;
    private final EditPhysicianValidator editPhysicianValidator;
    private  final EditPatientValidator editPatientValidator;
    File prescriptionTemplateFile = Paths.get("src/main/resources/medScriptFiles/RecipeTemplate.pdf").toAbsolutePath().toFile();
    @Autowired
    public AdminController(AccountRepository accountDetailsService, AuthService registrationService, AccountPhysicianValidator accountPhysicianValidator, Mapper mapper, AccountValidator accountValidator, PhysicianRepository physicianRepository, PatientRepository patientRepository, AccountAdminValidator accountAdminValidator, PrescriptionRepository prescriptionRepository, InsuranceRepository insuranceRepository, EditPhysicianValidator editPhysicianValidator, EditPatientValidator editPatientValidator) {
        this.accountRepository = accountDetailsService;
        this.registrationService = registrationService;
        this.accountPhysicianValidator = accountPhysicianValidator;
        this.mapper = mapper;
        this.accountValidator = accountValidator;
        this.physicianRepository = physicianRepository;
        this.patientRepository = patientRepository;
        this.accountAdminValidator = accountAdminValidator;
        this.prescriptionRepository = prescriptionRepository;
        this.insuranceRepository = insuranceRepository;
        this.editPhysicianValidator = editPhysicianValidator;
        this.editPatientValidator = editPatientValidator;
        this.accountRepository = accountRepository;
        userDetailsService = new AccountDetailsService(accountRepository);
    }

    // Retrieves the current account from the security context
    public Account getAccount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        AccountDetails user = (AccountDetails) userDetailsService.loadUserByUsername(currentPrincipalName);
        Account adminAccount = user.getAccount();
        return adminAccount;
    }
    /**
     * Retrieves admin details for the current admin account.
     *
     * @return ResponseEntity containing admin details
     */
    @GetMapping("/admin/getDetails")
    public ResponseEntity<Object> showDetails() {
        RequestRes res = new RequestRes();
        res.setStatusCode(200);
        Admin admin = (Admin) getAccount().getPerson();
        res.setAdmin(admin);
        res.setCreatorAdmin(admin.getCreatorAdmin());
        return ResponseEntity.ok(res);
    }
    /**
     * Creates a new admin account.
     *
     * @param requestRes   Request body containing the details for the new admin account
     * @param bindingResult Used for validation results
     * @return ResponseEntity with status of the operation
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping("/admin/createAdmin")
    public ResponseEntity<Object> createAdmin(@RequestBody RequestRes requestRes,
                                              BindingResult bindingResult) {
        RequestRes res = new RequestRes();
        AdminAccountDTO adminAccountDTO = mapper.mapToAdminAccountDTO(requestRes);

        Account account = adminAccountDTO.getAccount();

        account.setUnblocked(requestRes.getIsUnblocked());
        accountAdminValidator.validate(adminAccountDTO, bindingResult);
        if(bindingResult.hasErrors()) {
            res.setStatusCode(422);
            res.setBindingResult(bindingResult.getAllErrors());
            res.setMessage("Validation Error");
            return ResponseEntity.status(422).body(res);
        }
        Admin admin = adminAccountDTO.getAdmin();

        Account creatorAdminAccount = getAccount();
        Admin creatorAdmin = (Admin) creatorAdminAccount.getPerson();
        Institution institution = creatorAdmin.getInstitution(); // Assuming the institution is already associated with the creator admin
        account.setRole("ROLE_ADMIN");
        account.setCreationDate(Date.from(Instant.now()));
        registrationService.registerAdmin(account, admin, creatorAdmin, institution);
        res.setAccount(account);
        res.setAdmin(admin);
        res.setStatusCode(200);
        return ResponseEntity.ok(res);
    }
    /**
     * Edits the information of a patient by their ID.
     *
     * @param requestRes   Request body containing the updated patient details
     * @param id           ID of the patient to be edited
     * @param bindingResult Used for validation results
     * @return ResponseEntity with the result of the operation
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping("/admin/editPatientInfo/{id}")
    public ResponseEntity<Object> editPatient(@RequestBody RequestRes requestRes, @PathVariable("id")Long id, BindingResult bindingResult) {
        RequestRes res = new RequestRes();
        boolean samePassword = true;
        Optional<Patient> patientOptional = patientRepository.findById(id);
        if(patientOptional.isEmpty()) {
            res.setStatusCode(404);
            res.setMessage("Physician with this id not found!");
            return ResponseEntity.ok(res);
        }

        Patient patient = patientOptional.get();
        editPatientValidator.setPatientToUpdate(patient);
        PatientAccountDTO patientAccountDTO = mapper.mapToPatientAccountDTO(requestRes);
       // insuranceRepository.save(patient.getInsurance()); //delete afterwards
        patientAccountDTO.setInsuranceNumber(patient.getInsurance().getNumber());
        editPatientValidator.validate(patientAccountDTO, bindingResult);
        if (bindingResult.hasErrors()) {
            res.setStatusCode(422);
            res.setBindingResult(bindingResult.getAllErrors());
            res.setMessage("Validation Error");
            return ResponseEntity.status(422).body(res);
        }

        patient.getAccount().setUsername(requestRes.getUsername());
        if(requestRes.getPassword() == null) {
            // Password remains unchanged
            samePassword = true;
        } else if(requestRes.getPassword().trim().equals("")) {
            // Password remains unchanged
            samePassword = true;
        }else {
            //else change the password
            patient.getAccount().setPassword(requestRes.getPassword());
            samePassword = false;
        }

        patient.getAccount().setEmail(requestRes.getEmail());
        patient.getAccount().setPhoneNumber(requestRes.getPhoneNumber());
        patient.getAccount().setUnblocked(requestRes.getIsUnblocked());
        patient.setAddress(requestRes.getAddress());
        patient.setFirstname(requestRes.getFirstname());
        patient.setSecondName(requestRes.getSecondname());
        patient.setBirthDate(requestRes.getBirthDate());



        registrationService.editPatient(patient.getAccount(), patient, samePassword);
        res.setMessage("Physician changed successfully!");
        res.setStatusCode(200);
        return ResponseEntity.ok(res);
    }
    /**
     * Edits the information of a physician by their ID.
     *
     * @param requestRes   Request body containing the updated physician details
     * @param id           ID of the physician to be edited
     * @param bindingResult Used for validation results
     * @return ResponseEntity with the result of the operation
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping("/admin/editPhysicianInfo/{id}")
    public ResponseEntity<Object> editPhysician(@RequestBody RequestRes requestRes, @PathVariable("id")Long id, BindingResult bindingResult) {
        RequestRes res = new RequestRes();
        boolean samePassword = true;
        Optional<Physician> physicianOptional = physicianRepository.findById(id);
        if(physicianOptional.isEmpty()) {
            res.setStatusCode(404);
            res.setMessage("Physician with this id not found!");
            return ResponseEntity.ok(res);
        }

        Physician physician = physicianOptional.get();
        editPhysicianValidator.setPhysicianToUpdate(physician);
        PhysicianAccountDTO physicianAccountDTO = mapper.mapToPhysicianAccountDTO(requestRes);
        editPhysicianValidator.validate(physicianAccountDTO, bindingResult);
        if (bindingResult.hasErrors()) {
            res.setStatusCode(422);
            res.setBindingResult(bindingResult.getAllErrors());
            res.setMessage("Validation Error");
            return ResponseEntity.status(422).body(res);
        }

        physician.getAccount().setUsername(requestRes.getUsername());
        if(requestRes.getPassword() == null) {
           // Password remains unchanged
            samePassword = true;
        } else if(requestRes.getPassword().trim().equals("")) {
            // Password remains unchanged
            samePassword = true;
        }else {
            //else change the password
            physician.getAccount().setPassword(requestRes.getPassword());
            samePassword = false;
        }

        physician.getAccount().setEmail(requestRes.getEmail());
        physician.getAccount().setPhoneNumber(requestRes.getPhoneNumber());
        physician.getAccount().setUnblocked(requestRes.getIsUnblocked());
        physician.setAddress(requestRes.getAddress());
        physician.setFirstname(requestRes.getFirstname());
        physician.setSecondName(requestRes.getSecondname());
        physician.setBirthDate(requestRes.getBirthDate());



        registrationService.editPhysician(physician.getAccount(), physician, samePassword);
        res.setMessage("Physician changed successfully!");
        res.setStatusCode(200);
        return ResponseEntity.ok(res);
    }
    /**
     * Creates a new physician account based on the provided details.
     *
     * @param requestRes   the details for creating a new physician account.
     * @param bindingResult contains validation results.
     * @return a ResponseEntity containing the result of the operation.
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping("/admin/createPhysician")
    public ResponseEntity<Object> createPhysician(@RequestBody RequestRes requestRes, BindingResult bindingResult) {
        RequestRes res = new RequestRes();
        PhysicianAccountDTO physicianAccountDTO = mapper.mapToPhysicianAccountDTO(requestRes);
        accountPhysicianValidator.validate(physicianAccountDTO, bindingResult);
        if (bindingResult.hasErrors()) {
            res.setStatusCode(422);
            res.setBindingResult(bindingResult.getAllErrors());
            res.setMessage("Validation Error");
            return ResponseEntity.status(422).body(res);
        }

        Physician physician = new Physician();
        Account account = new Account();
        account.setUsername(requestRes.getUsername());
        account.setPassword(requestRes.getPassword());
        account.setEmail(requestRes.getEmail());
        account.setPhoneNumber(requestRes.getPhoneNumber());
        account.setUnblocked(requestRes.getIsUnblocked());
        physician.setAddress(requestRes.getAddress());
        physician.setFirstname(requestRes.getFirstname());
        physician.setSecondName(requestRes.getSecondname());
        physician.setBirthDate(requestRes.getBirthDate());


        Account creatorAdminAccount = getAccount();
        Admin creatorAdmin = (Admin) creatorAdminAccount.getPerson();
        physician.setCreatorAdmin(creatorAdmin);
        physician.setInstitution(creatorAdmin.getInstitution());
        account.setCreationDate(Date.from(Instant.now()));
        account.setRole("ROLE_PHYSICIAN");
        account.setPerson(physician);
        physician.setAccount(account);
        registrationService.registerPhysician(account, physician, creatorAdmin, creatorAdmin.getInstitution());


    res.setPhysician(physician);
    res.setMessage("Successfully created physician account!");
    res.setStatusCode(200);
    return ResponseEntity.ok(res);
    }
    /**
     * Retrieves the prescription details for the given prescription ID.
     *
     * @param id the ID of the prescription to retrieve.
     * @return a ResponseEntity containing the prescription details or an error message.
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping("/admin/prescription/{id}")
    public ResponseEntity<Object> getPrescription(@PathVariable("id")Long id) {
        RequestRes requestRes = new RequestRes();
        Optional<Prescription> prescription = prescriptionRepository.findById(id);
//        Account patientAccount = getAccount();
//        Patient patient = (Patient) patientAccount.getPerson();
        if(prescription.isEmpty()) {
            requestRes.setError("404");
            requestRes.setMessage("Prescription Not Found");
            requestRes.setStatusCode(404);
            return ResponseEntity.status(404).body(requestRes);
        } else {

            requestRes.setStatusCode(200);
            requestRes.setPrescription(prescription.get());
            requestRes.setPhysicianDTO(new PhysicianDTO(prescription.get().getPhysician()));
            requestRes.setPatientDTO(new PatientDTO(prescription.get().getPatient()));
            return ResponseEntity.status(200).body(requestRes);
        }
    }
    /**
     * Retrieves a sorted list of prescriptions for the specified physician.
     *
     * @param id    the ID of the physician whose prescriptions are to be retrieved.
     * @param sort  the field to sort by (optional).
     * @param size  the number of prescriptions per page (optional).
     * @param page  the page number to retrieve (optional).
     * @return a ResponseEntity containing the sorted list of prescriptions.
     */
    @GetMapping("/admin/physician/prescriptionsSorted/{id}")
    public ResponseEntity<Object> getPhysiciansSortedPagePrescriptions(
            @PathVariable("id")Long id,
            @RequestParam("sortField") @Nullable String sort,
            @RequestParam("size") @Nullable Integer size,
            @RequestParam("page") @Nullable Integer page) {

        RequestRes res = new RequestRes();

        // Fetch physician information
        Physician physician;
        // Fetch patient information
        Optional<Physician> optionalPhysician = physicianRepository.findById(id);
        if (optionalPhysician.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Physician not found");
        } else {
            physician = optionalPhysician.get();
        }

        // Determine current page and page size
        int currentPage = (page != null && page >= 0) ? page : 0;
        int pageSize = (size != null && size > 0) ? size : 5; // Ensure pageSize is at least 1

        // Determine sort field
        String sortField = (sort == null || sort.trim().isEmpty()) ? "creationDate" : sort;
        Comparator<Prescription> comparator;
        if ("expirationDate".equals(sortField)) {
            comparator = Comparator.comparing(Prescription::getExpirationDate, Comparator.nullsLast(Comparator.naturalOrder())).reversed();
        } else if ("medication".equals(sortField)) {
            comparator = Comparator.comparing(prescription -> {
                String medication = prescription.getMedication();
                return medication != null ? medication.toLowerCase() : "";
            }, Comparator.nullsLast(Comparator.naturalOrder()));
        } else {
            comparator = Comparator.comparing(Prescription::getCreationDate, Comparator.nullsLast(Comparator.naturalOrder())).reversed();
        }

        // Sort and paginate prescriptions
        List<Prescription> allPrescriptions = physician.getPrescriptions().stream()
                .sorted(comparator)
                .collect(Collectors.toList());

        int start = currentPage * pageSize;
        int end = Math.min(start + pageSize, allPrescriptions.size());
        List<Prescription> paginatedPrescriptions;
        try {
            paginatedPrescriptions = allPrescriptions.subList(start, end);
        } catch (IllegalArgumentException e) {
            paginatedPrescriptions = Collections.emptyList();
        }

        // Create PageImpl object with paginated data
        Page<Prescription> pageImpl = new PageImpl<>(paginatedPrescriptions, PageRequest.of(currentPage, pageSize), allPrescriptions.size());

        res.setPagedAndSortedPatientPrescriptions(pageImpl.getContent());
        res.setPrescriptionPagesSize(pageImpl.getTotalPages());
        res.setStatusCode(200);
        return ResponseEntity.ok(res);
    }
    /**
     * Retrieves a sorted list of prescriptions for the specified patient.
     *
     * @param id    the ID of the patient whose prescriptions are to be retrieved.
     * @param sort  the field to sort by (optional).
     * @param size  the number of prescriptions per page (optional).
     * @param page  the page number to retrieve (optional).
     * @return a ResponseEntity containing the sorted list of prescriptions.
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping("/admin/patient/prescriptionsSorted/{id}")
    public ResponseEntity<Object> getPatientSortedPagePrescriptions(
            @PathVariable("id")Long id,
            @RequestParam("sortField") @Nullable String sort,
            @RequestParam("size") @Nullable Integer size,
            @RequestParam("page") @Nullable Integer page) {

        RequestRes res = new RequestRes();
        Patient patient;
        // Fetch patient information
        Optional<Patient> optionalPatient = patientRepository.findById(id);
        if (optionalPatient.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Patient not found");
        } else {
            patient = optionalPatient.get();
        }

        // Determine current page and page size
        int currentPage = (page != null && page >= 0) ? page : 0;
        int pageSize = (size != null && size > 0) ? size : 5; // Ensure pageSize is at least 1

        // Determine sort field
        String sortField = (sort == null || sort.trim().isEmpty()) ? "creationDate" : sort;
        Comparator<Prescription> comparator;
        if ("expirationDate".equals(sortField)) {
            comparator = Comparator.comparing(Prescription::getExpirationDate).reversed();
        } else if ("medication".equals(sortField)) {
            comparator = Comparator.comparing(prescription -> prescription.getMedication().toLowerCase());
        } else {
            comparator = Comparator.comparing(Prescription::getCreationDate).reversed();
        }

        // Sort and paginate prescriptions
        List<Prescription> allPrescriptions = patient.getPrescriptions().stream()
                .sorted(comparator)
                .collect(Collectors.toList());

        int start = currentPage * pageSize;
        int end = Math.min(start + pageSize, allPrescriptions.size());
        List<Prescription> paginatedPrescriptions = new LinkedList<>();
        try {
            paginatedPrescriptions = allPrescriptions.subList(start, end);
        } catch (IllegalArgumentException e) {

        }


        // Create PageImpl object with paginated data
        Page<Prescription> pageImpl = new PageImpl<>(paginatedPrescriptions, PageRequest.of(currentPage, pageSize), allPrescriptions.size());

        res.setPagedAndSortedPatientPrescriptions(pageImpl.getContent());
        res.setPrescriptionPagesSize(pageImpl.getTotalPages());
        res.setStatusCode(200);
        return ResponseEntity.ok(res);
    }
    /**
     * Retrieves the details of a patient by their ID.
     *
     * @param patientId the ID of the patient to retrieve.
     * @return a ResponseEntity containing the patient details or an error message.
     */
    @GetMapping("/admin/patient/details/{id}")
    public ResponseEntity<Object> getPatientDetails(@PathVariable("id")Long patientId) {
        RequestRes res = new RequestRes();
        Optional<Patient> patientOptional = patientRepository.findById(patientId);
        Patient patient;
        if(!patientOptional.isEmpty()) {
            patient = patientOptional.get();
            res.setStatusCode(200);
            res.setPatient(patient);
        } else if(patientOptional.isEmpty()) {
            res.setMessage("Patient with id: " + patientId+ " not found");
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
        }
        return ResponseEntity.status(200).body(res);

    }
    /**
     * Retrieves the details of a physician by their ID.
     *
     * @param physicianId the ID of the physician to retrieve.
     * @return a ResponseEntity containing the physician details or an error message.
     */
    @GetMapping("/admin/physician/details/{id}")
    public ResponseEntity<Object> getPhysicianDetails(@PathVariable("id")Long physicianId) {
        RequestRes res = new RequestRes();
        Optional<Physician> patientOptional = physicianRepository.findById(physicianId);
        Physician physician;
        if(!patientOptional.isEmpty()) {
            physician = patientOptional.get();
            res.setStatusCode(200);
            res.setPhysician(physician);
        } else if(patientOptional.isEmpty()) {
            res.setMessage("Physician with id: " + physicianId+ " not found");
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
        }
        return ResponseEntity.status(200).body(res);

    }
    /**
     * Lists all patients, optionally filtering by a search string.
     *
     * @param searchString a string to filter patients by name, email, address, or birth date (optional).
     * @return a ResponseEntity containing the list of patients.
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping("/admin/listPatients")
    public ResponseEntity<Object> listPatients(@RequestParam(name = "searchString", required = false)String searchString) {
        RequestRes res = new RequestRes();
        res.setMessage("All existent Patients");
        List<Patient> patients = patientRepository.findAll();

            if(searchString == null) {
                searchString = "";
            }
        String finalSearchString = searchString;
        patients = patients.stream().filter(patient -> patient.getFullName().trim().toLowerCase().contains(finalSearchString.trim().toLowerCase())
                    || patient.getAccount().getEmail().trim().toLowerCase().contains(finalSearchString.trim().toLowerCase()) ||
                    patient.getAddress().trim().toLowerCase().contains(finalSearchString.trim().toLowerCase())
                    || patient.getBirthDate().toString().contains(finalSearchString.trim().toLowerCase())).toList();

        Comparator<Patient> comparator = Comparator.comparing(patient -> {
            String secondName = patient.getSecondName();
            return secondName != null ? secondName.toLowerCase() : "";
        }, Comparator.nullsLast(Comparator.naturalOrder()));
        res.setAllPatients(patients.stream().sorted(comparator).collect(Collectors.toList()));

        return ResponseEntity.ok(res);
    }
    /**
     * Lists all patients, optionally filtering by a search string.
     *
     * @param searchString a string to filter patients by name, email, address, or birth date (optional).
     * @return a ResponseEntity containing the list of patients.
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping("/admin/listPhysicians")
    public ResponseEntity<Object> listPhysicians(@RequestParam(name = "searchString", required = false)String searchString) {
        RequestRes res = new RequestRes();
        res.setMessage("All existent Patients");
        List<Physician> physicians = physicianRepository.findAll();
        if(searchString == null) {
            searchString = "";
        }

        String finalSearchString = searchString;
        physicians = physicians.stream().filter(physician -> physician.getFullName().trim().toLowerCase().contains(finalSearchString.trim().toLowerCase())
                    || physician.getAccount().getEmail().trim().toLowerCase().contains(finalSearchString.trim().toLowerCase()) ||
                physician.getAddress().trim().toLowerCase().contains(finalSearchString.trim().toLowerCase())
                    || physician.getBirthDate().toString().contains(finalSearchString.trim().toLowerCase())
                    || physician.getAccount().getPhoneNumber().toString().trim().toLowerCase().contains(finalSearchString.trim().toLowerCase())).toList()
        ;



        Comparator<Physician> comparator = Comparator.comparing(physician -> {
            String secondName = physician.getSecondName();
            return secondName != null ? secondName.toLowerCase() : "";
        }, Comparator.nullsLast(Comparator.naturalOrder()));

        res.setAllPhysicians(physicians.stream().sorted(comparator).collect(Collectors.toList()));

        return ResponseEntity.ok(res);
    }
    /**
     * Lists all patients assigned to a specific physician.
     *
     * @param physicianId  the ID of the physician.
     * @param searchString a string to filter patients by name, email, address, or birth date (optional).
     * @return a ResponseEntity containing the list of assigned patients.
     */
    @GetMapping("/admin/physician/listAssignedPatients/{id}")
    public ResponseEntity<Object> listAssignedPatients(@PathVariable("id")Long physicianId,
                                                       @RequestParam(name = "searchString", required = false)String searchString) {
        RequestRes res = new RequestRes();
        Optional<Physician> physicianOptional = physicianRepository.findById(physicianId);
        if(physicianOptional.isEmpty()) {
            res.setStatusCode(404);
            res.setMessage("Physician with this id not found!");
            return ResponseEntity.ok(res);
        }

        Physician physician = physicianOptional.get();
        res.setMessage("Assigned Patients");
        List<Patient> patients = physician.getPatients().stream().toList();
        if(searchString == null) {
            searchString = "";
        }
         //double not-null check

            String finalSearchString = searchString;
            patients = patients.stream().filter(patient -> patient.getFullName().trim().toLowerCase().contains(finalSearchString.trim().toLowerCase())
                        || patient.getAccount().getEmail().trim().toLowerCase().contains(finalSearchString.trim().toLowerCase()) ||
                        patient.getAddress().trim().toLowerCase().contains(finalSearchString.trim().toLowerCase())
                        || patient.getBirthDate().toString().contains(finalSearchString.trim().toLowerCase())
                        || patient.getAccount().getPhoneNumber().contains(finalSearchString.trim().toLowerCase())
            ||  patient.getAccount().getPhoneNumber().toString().trim().toLowerCase().contains(finalSearchString.trim().toLowerCase())).toList()
                ;

        res.setAllPatients(patients.stream().sorted( (first, second) -> second.getSecondName().compareTo(first.getSecondName())).toList());
        res.setStatusCode(200);
        return ResponseEntity.ok(res);
    }
    /**
     * Downloads the prescription as a PDF file.
     *
     * @param response the HttpServletResponse object used to set response headers and content type.
     * @param id the ID of the prescription to download.
     * @return a ResponseEntity containing the PDF file as a resource, or a 404 response if the prescription is not found.
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @RequestMapping(value = "/admin/downloadPrescription/{id}", method = RequestMethod.GET, produces =   { MediaType.APPLICATION_OCTET_STREAM_VALUE})
    @ResponseBody
    public ResponseEntity<Object> getFile(HttpServletResponse response, @PathVariable("id")Long id) {
        RequestRes res = new RequestRes();
        Optional<Prescription> prescriptionOptional = prescriptionRepository.findById(id);
        if(prescriptionOptional.isEmpty()) {
            res.setMessage("Prescription not found");
            res.setStatusCode(404);
            return ResponseEntity.notFound().build();
        }
        Prescription prescription = prescriptionOptional.get();
        File prescriptionResultFile = Paths.get("src/main/resources/medScriptFiles/RecipeResult" + prescription.getId() + ".pdf")
                .toAbsolutePath().toFile();
        try {
            PrescriptionPdfGenerator.createPdf(prescription, prescriptionTemplateFile.getAbsolutePath(),
                    prescriptionResultFile.getAbsolutePath(), prescription.getPhysician());


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // Set the content type to octet-stream
        response.setContentType("application/octet-stream");
        // Set the suggested file name with the .ct extension
        response.setHeader("Content-Disposition", "attachment; filename=data.ct");

        return ResponseEntity.ok(new FileSystemResource(prescriptionResultFile));

    }


}
