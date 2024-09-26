package group11.medScriptAPI.controller;

import group11.medScriptAPI.DTO.*;
import group11.medScriptAPI.entity.*;
import group11.medScriptAPI.repository.PatientRepository;
import group11.medScriptAPI.repository.PhysicianRepository;
import group11.medScriptAPI.repository.PrescriptionRepository;
import group11.medScriptAPI.util.PrescriptionPdfGenerator;
import group11.medScriptAPI.util.PrescriptionValidator;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Controller for handling physician-related requests.
 */
@RestController
@CrossOrigin
public class PhysicianController {

    private final PhysicianRepository physicianRepository;
    private final PatientRepository patientRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final UserDetailsService userDetailsService;
    private final Comparator<Prescription> comparingExpirationDate= Comparator.comparing(Prescription::getExpirationDate);
    private final PrescriptionValidator prescriptionValidator;
    File prescriptionTemplateFile = Paths.get("src/main/resources/medScriptFiles/RecipeTemplate.pdf").toAbsolutePath().toFile();
    public PhysicianController(PhysicianRepository physicianRepository, PatientRepository patientRepository, PrescriptionRepository prescriptionRepository, UserDetailsService userDetailsService, PrescriptionValidator prescriptionValidator) {
        this.physicianRepository = physicianRepository;
        this.patientRepository = patientRepository;
        this.prescriptionRepository = prescriptionRepository;
        this.userDetailsService = userDetailsService;
        this.prescriptionValidator = prescriptionValidator;
    }
    /**
     * Retrieves the currently authenticated physician's account details.
     *
     * @return the Account of the currently authenticated physician.
     */
    public Account getAccount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        AccountDetails user = (AccountDetails) userDetailsService.loadUserByUsername(currentPrincipalName);
        Account physicianAccount = user.getAccount();
        return physicianAccount;
    }
    /**
     * Gets the details of the currently authenticated physician.
     *
     * @return ResponseEntity containing physician information.
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping("physician/getDetails")
    public ResponseEntity<Object> getPhysicianInfo() {
        RequestRes res = new RequestRes();
        Account physicianAccount = getAccount();
        Physician physician = (Physician) physicianAccount.getPerson();
        res.setStatusCode(200);
        res.setPhysician(physician);
        return ResponseEntity.ok(res);
    }
    /**
     * Uploads the physician's signature.
     *
     * @param multipartFile the signature file to upload.
     * @return ResponseEntity indicating success or failure.
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping("physician/uploadSignature")
    public ResponseEntity<Object> uploadSignature(@RequestParam("signature")MultipartFile multipartFile) {
        RequestRes res = new RequestRes();
        if(multipartFile != null) {
            Physician physician = (Physician) getAccount().getPerson();
           // if (physician.getSignatureFile().exists()) {
                if (physician.getSignatureFile() != null) {
                System.out.println( physician.getSignatureFile().delete());
                physician.setSignatureFile(Paths.get("src/main/resources/physicianSignatures/" + physician.getId() + ".png").toAbsolutePath().toFile());
            }
            File savedSignature = Paths.get("src/main/resources/physicianSignatures/" + physician.getId() + ".png").toAbsolutePath().toFile();
            try {
                multipartFile.transferTo(savedSignature);
                physician.setSignatureFile(savedSignature);
                physicianRepository.save(physician);
                res.setMessage("Could not upload signature");
                ResponseEntity.status(500).body(res);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

        res.setMessage("Successfully uploaded signature");
        return  ResponseEntity.ok(res);
    }
    /**
     * Edits the details of the currently authenticated physician.
     *
     * @param req the request containing updated physician information.
     * @return ResponseEntity containing the updated physician information.
     */
    @PostMapping("/physician/editInfo")
    public ResponseEntity<Object> editPhysicianInfo(@RequestBody RequestRes req) {
        RequestRes res = new RequestRes();
        Account physicianAccount = getAccount();
        Physician physician = (Physician) physicianAccount.getPerson();
        if(req.getFirstname() != null)
        physician.setFirstname(req.getFirstname());
        if(req.getSecondname() != null)
        physician.setSecondName(req.getSecondname());
        if(req.getAddress() != null)
        physician.setAddress(req.getAddress());
        if(req.getBirthDate() != null)
        physician.setBirthDate(req.getBirthDate());
        physician = physicianRepository.save(physician);

        res.setPhysician(physician);
        res.setStatusCode(200);
        return ResponseEntity.ok(res);
    }
    /**
     * Lists all patients associated with the physician.
     *
     * @param searchString optional search string to filter patients.
     * @return ResponseEntity containing a list of patients.
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping("/physician/listPatients")
    public ResponseEntity<Object> listPatients(@RequestParam(name = "searchString", required = false)String searchString) {
        RequestRes res = new RequestRes();
        res.setMessage("All existent Patients");
        List<Patient> patients = patientRepository.findAll();
        if(searchString != null && searchString.trim().equals("")) {

        }
       else if(searchString != null) {
            patients = patients.stream().filter(patient -> patient.getFullName().trim().toLowerCase().contains(searchString.trim().toLowerCase())
                    || patient.getAccount().getEmail().trim().toLowerCase().contains(searchString.trim().toLowerCase()) ||
                patient.getAddress().trim().toLowerCase().contains(searchString.trim().toLowerCase())
                    || patient.getBirthDate().toString().contains(searchString.trim().toLowerCase())).toList();
        }
        Comparator<Patient> comparator = Comparator.comparing(patient -> {
            String secondName = patient.getSecondName();
            return secondName != null ? secondName.toLowerCase() : "";
        }, Comparator.nullsLast(Comparator.naturalOrder()));
        res.setAllPatients(patients.stream().sorted(comparator).collect(Collectors.toList()));

        return ResponseEntity.ok(res);
    }
    /**
     * Retrieves details of a specific patient by ID.
     *
     * @param id the ID of the patient.
     * @return ResponseEntity containing patient details.
     */
    @GetMapping("/physician/patient/{id}")
    public ResponseEntity<Object> getPatient(@PathVariable("id")Long id) {
        RequestRes res = new RequestRes();
        Account physicianAccount = getAccount();
        Physician physician = (Physician) physicianAccount.getPerson();
        res.setMessage("Patient Details");
        Optional<Patient> patientOptional = patientRepository.findById(id);
        if(patientOptional.isEmpty()) {
            res.setStatusCode(404);
            res.setMessage("Patient with id " + id + " not found!" );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
        } else {
            res.setPatient(patientOptional.get());
            res.setPhysicianIds(patientOptional.get().getPhysicians().stream().map(physician1 -> physician1.getId()).toList());
            res.setStatusCode(200);
        }

        return ResponseEntity.ok(res);
    }
    /**
     * Lists all patients assigned to the currently authenticated physician.
     *
     * @return ResponseEntity containing a list of assigned patients.
     */
    @GetMapping("/physician/listAssignedPatients")
    public ResponseEntity<Object> listAssignedPatients() {
        RequestRes res = new RequestRes();
        Account physicianAccount = getAccount();
        Physician physician = (Physician) physicianAccount.getPerson();
        res.setMessage("Assigned Patients");
        res.setAllPatients(physician.getPatients().stream().sorted( (first, second) -> second.getSecondName().compareTo(first.getSecondName())).toList());
    res.setStatusCode(200);
        return ResponseEntity.ok(res);
    }
    /**
     * Assigns a patient to the currently authenticated physician.
     *
     * @param id the ID of the patient to be assigned.
     * @return ResponseEntity containing the assigned patient details.
     */
    @GetMapping("/physician/assignPatient/{id}")
    public  ResponseEntity<Object> assignPatient(@PathVariable("id")Long id) {
        RequestRes res = new RequestRes();
        Optional<Patient> patient = patientRepository.findById(id);
        Account physicianAccount = getAccount();
        Physician physician = (Physician) physicianAccount.getPerson();
        if (! patient.isEmpty()) {

            Patient patient1 = patient.get();
            physician.getPatients().add(patient1);
            patient1.getPhysicians().add(physician);
            patientRepository.save(patient1);
            physicianRepository.save(physician);
            System.out.println("Assignment");
            System.out.println("Physician got patients: " );
            physician.getPatients().stream().forEach(patient2 -> System.out.println(patient2.getId()));
            System.out.println("Patient got physician: " );
            patient1.getPhysicians().stream().forEach(physician1 -> System.out.println(physician1.getId()));

            res.setPatient(patient1);
            return ResponseEntity.ok(res);
        }
        res.setMessage("Patient does not exist!");

        return ResponseEntity.status(500).body(res);
    }
    /**
     * Removes a patient from the physician's patient list.
     *
     * @param id the ID of the patient to be removed.
     * @return ResponseEntity indicating the result of the removal process.
     */
    @GetMapping("/physician/removePatient/{id}")
    public ResponseEntity<Object> removePatient(@PathVariable("id")Long id) {
        RequestRes res = new RequestRes();
        Optional<Patient> patient = patientRepository.findById(id);
        Account physicianAccount = getAccount();
        Physician physician = (Physician) physicianAccount.getPerson();
        if (! patient.isEmpty() && physician.getPatients().contains(patient.get())) {

            Patient patient1 = patient.get();
            physician.getPatients().remove(patient1);
            patient1.getPhysicians().remove(physician);
            patientRepository.save(patient1);
            physicianRepository.save(physician);
            res.setMessage("Patient successfully deleted!");
            res.setPatient(patient1);
            return ResponseEntity.ok(patient1);
        }
        res.setMessage("Patient does not exist or was not assigned");

        return ResponseEntity.status(500).body(res);
    }
    /**
     * Creates a new prescription for a specified patient.
     *
     * @param id the ID of the patient for whom the prescription is created.
     * @param requestRes the request body containing prescription details.
     * @param bindingResult holds validation errors, if any.
     * @return ResponseEntity indicating the result of the prescription creation process.
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping("/physician/prescribe/{id}")
    public ResponseEntity<Object> addPrescriptionSubmit(@PathVariable("id") Long id,
                                                        @RequestBody RequestRes requestRes,
                                                        BindingResult bindingResult) {
        RequestRes res = new RequestRes();
        Prescription prescription = new Prescription();

        Optional<Patient> patient = patientRepository.findById(id);
        Account physicianAccount = getAccount();
        Physician physician = (Physician) physicianAccount.getPerson();
        if (! patient.isEmpty()) {
            Patient patient1 = patient.get();
            prescription.setPatient(patient1);
            prescription.setPhysician(physician);
            prescription.setCreationDate(Date.from(Instant.now()));
            prescription.setMedication(requestRes.getMedication());
            prescription.setExpirationDate(requestRes.getExpirationDate());
            prescription.setTotalGrammage(requestRes.getTotalGrammage());
            prescription.setText(requestRes.getText());
            prescriptionValidator.validate(prescription, bindingResult);
            if(bindingResult.hasErrors()) {
                res.setStatusCode(422);
                res.setBindingResult(bindingResult.getAllErrors());
                res.setMessage("Validation Error");
                return ResponseEntity.status(422).body(res);
            }
            res.setPrescription(prescriptionRepository.save(prescription));
            patientRepository.findById(id).get().getPrescriptions().forEach(prescription1 -> System.out.println(prescription1.getId()));

            System.out.println(patientRepository.findById(id).get().getPrescriptions().toString());
            System.out.println(patientRepository.findById(id).get().getPrescriptions().toString());

            res.setMessage("Successfully prescribed!");
            res.setStatusCode(200);
            return ResponseEntity.ok(res);

        }
        res.setMessage("Prescription not found");
        return ResponseEntity.status(401).body(res);
    }
    /**
     * Retrieves valid prescriptions for a specified patient.
     *
     * @param id the ID of the patient.
     * @return ResponseEntity containing valid prescriptions for the patient.
     */
    @GetMapping("/physician/validPrescriptions/{id}")
    public ResponseEntity<Object> showValidPrescriptions( @PathVariable("id")Long id) {
        RequestRes res = new RequestRes();
        Optional<Patient> patient = patientRepository.findById(id);
        Account physicianAccount = getAccount();
        Physician physician = (Physician) physicianAccount.getPerson();
        if(!patient.isEmpty()) {
            Patient patient1 = patient.get();
            List<Prescription> prescriptions = patient1.getPrescriptions().stream().filter(prescription ->
                            prescription.getPatient().equals(patient1) && prescription.getPhysician().equals(physician)
                                    && prescription.getExpirationDate().after(Date.from(Instant.now()))).
                    collect(Collectors.toList());
            List<PrescriptionDetailsDTO> prescriptionDetailsDTOList = new ArrayList<>();
            prescriptions = prescriptions.stream().sorted(comparingExpirationDate.reversed()).toList();
            res.setPrescriptions(prescriptions);
            res.setStatusCode(200);
            return ResponseEntity.ok(res);
        }
        res.setMessage("Prescription not found");
        return ResponseEntity.status(401).body(res);
    }
    /**
     * Retrieves detailed information about a specific prescription.
     *
     * @param id the ID of the prescription.
     * @return ResponseEntity containing the prescription details.
     */
    @GetMapping("/physician/prescription/{id}")
    public ResponseEntity<Object>showPrescription( @PathVariable("id")Long id) {
        RequestRes res = new RequestRes();
        Optional<Prescription> prescription = prescriptionRepository.findById(id);
        if(!prescription.isEmpty()) {
            res.setStatusCode(200);
            PersonDTO personDTO = new PersonDTO(prescription.get().getPhysician());
            res.setPatientDTO(new PatientDTO((Person)prescription.get().getPatient()));
            res.setPrescription(prescription.get());
            res.setPhysicianDTO(new PhysicianDTO(prescription.get().getPhysician()));
            return ResponseEntity.ok(res);
        }
        res.setMessage("Prescription not found");
        res.setStatusCode(404);

        return ResponseEntity.status(404).body(res);
    }
    /**
     * Retrieves all prescriptions for a specified patient with pagination and sorting options.
     *
     * @param id the ID of the patient.
     * @param sort the field to sort by (optional).
     * @param size the size of each page (optional).
     * @param page the page number (optional).
     * @return ResponseEntity containing paginated and sorted prescriptions for the patient.
     */
    @GetMapping("/physician/allPrescriptions/{id}")
    public ResponseEntity<Object> showAllDescriptions( @PathVariable("id")Long id,
                                                       @RequestParam("sortField") @Nullable String sort,
                                                       @RequestParam("size") @Nullable Integer size,
                                                       @RequestParam("page") @Nullable Integer page) {
        RequestRes res = new RequestRes();
        Optional<Patient> patient = patientRepository.findById(id);
        Account physicianAccount = getAccount();
        Physician physician = (Physician) physicianAccount.getPerson();
        String sortField = (sort == null || sort.trim().isEmpty()) ? "creationDate" : sort;
        int currentPage = (page != null && page >= 0) ? page : 0;
        int pageSize = (size != null && size > 0) ? size : 5; // Ensure pageSize is at least 1
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

        if(!patient.isEmpty()) {
            Patient patient1 = patient.get();
            List<Prescription> allPrescriptions = patient1.getPrescriptions().stream().filter(prescription ->
                            prescription.getPatient().equals(patient1) && prescription.getPhysician().equals(physician)
                    ).sorted(comparator).collect(Collectors.toList());

            int start = currentPage * pageSize;
            int end = Math.min(start + pageSize, allPrescriptions.size());
            List<Prescription> paginatedPrescriptions;
            try {
                paginatedPrescriptions = allPrescriptions.subList(start, end);
            } catch (IllegalArgumentException e) {
                paginatedPrescriptions = Collections.emptyList();
            }

            Page<Prescription> pageImpl = new PageImpl<>(paginatedPrescriptions, PageRequest.of(currentPage, pageSize), allPrescriptions.size());

            res.setPagedAndSortedPatientPrescriptions(pageImpl.getContent());
            res.setPrescriptionPagesSize(pageImpl.getTotalPages());
            return ResponseEntity.ok(res);
        }
        res.setMessage("Patient not found");
        return ResponseEntity.status(401).body(res);
    }
    /**
     * Retrieves sorted and paginated prescriptions for the currently authenticated physician.
     *
     * @param sort the field to sort by (optional).
     * @param size the size of each page (optional).
     * @param page the page number (optional).
     * @return ResponseEntity containing sorted and paginated prescriptions.
     */
    @GetMapping("/physician/prescriptionsSorted")
    public ResponseEntity<Object> getSortedPagePrescriptions(
            @RequestParam("sortField") @Nullable String sort,
            @RequestParam("size") @Nullable Integer size,
            @RequestParam("page") @Nullable Integer page) {

        RequestRes res = new RequestRes();

        // Fetch physician information
        Physician physician = physicianRepository.findById(getAccount().getPerson().getId()).orElse(null);
        if (physician == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Physician not found");
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
     * Downloads the prescription document for a specified prescription ID.
     *
     * @param response the HTTP response to send the file.
     * @param id the ID of the prescription to download.
     * @return ResponseEntity indicating the result of the download process.
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @RequestMapping(value = "/physician/downloadPrescription/{id}", method = RequestMethod.GET, produces =   { MediaType.APPLICATION_OCTET_STREAM_VALUE})
    @ResponseBody
    public ResponseEntity<Object> getFilePhysician(HttpServletResponse response, @PathVariable("id")Long id) {
        RequestRes res = new RequestRes();
        Optional<Prescription> prescriptionOptional = prescriptionRepository.findById(id);
        if(prescriptionOptional.isEmpty()) {
            res.setMessage("Prescription not found");
            res.setStatusCode(404);
            return ResponseEntity.notFound().build();
        }
        Prescription prescription = prescriptionOptional.get();
        Physician physician = (Physician) getAccount().getPerson();

        if(prescription.getPhysician().getId() != prescription.getPhysician().getId()) {
            res.setMessage("Prescription does not belong to the physician!");
            res.setStatusCode(403);
            return ResponseEntity.ok(res);
        }
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
