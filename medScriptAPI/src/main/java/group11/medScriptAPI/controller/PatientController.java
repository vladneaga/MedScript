package group11.medScriptAPI.controller;

import group11.medScriptAPI.DTO.PatientDTO;
import group11.medScriptAPI.DTO.PatientDetailsDTO;
import group11.medScriptAPI.DTO.PhysicianDTO;
import group11.medScriptAPI.DTO.RequestRes;
import group11.medScriptAPI.entity.*;
import group11.medScriptAPI.repository.AccountRepository;
import group11.medScriptAPI.repository.InsuranceRepository;
import group11.medScriptAPI.repository.PatientRepository;
import group11.medScriptAPI.repository.PrescriptionRepository;
import group11.medScriptAPI.service.AccountDetailsService;
import group11.medScriptAPI.service.AuthService;
import group11.medScriptAPI.service.PrescriptionService;
import group11.medScriptAPI.util.PrescriptionPdfGenerator;
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
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class PatientController {
    private final AccountRepository accountRepository;
    private final AccountDetailsService userDetailsService;
    private final AuthService registrationService; //former registrationService
    private final PrescriptionRepository prescriptionRepository;
    private final PrescriptionService prescriptionService;
    private final PatientRepository patientRepository;
    private final InsuranceRepository insuranceRepository;
    private final PrescriptionPdfGenerator prescriptionPdfGenerator;

    File prescriptionTemplateFile = Paths.get("src/main/resources/medScriptFiles/RecipeTemplate.pdf").toAbsolutePath().toFile();
//    File prescriptionResultFile = Paths.get("src/main/resources/medScriptFiles/RecipeResult.pdf").toAbsolutePath().toFile();
@Autowired
    public PatientController(AccountRepository accountRepository, AccountDetailsService userDetailsService, AuthService registrationService, PrescriptionRepository prescriptionRepository, PrescriptionService prescriptionService, PatientRepository patientRepository, InsuranceRepository insuranceRepository, PrescriptionPdfGenerator prescriptionPdfGenerator) {
        this.accountRepository = accountRepository;
        this.userDetailsService = userDetailsService;
        this.registrationService = registrationService;
        this.prescriptionRepository = prescriptionRepository;
        this.prescriptionService = prescriptionService;
        this.patientRepository = patientRepository;
        this.insuranceRepository = insuranceRepository;
    this.prescriptionPdfGenerator = prescriptionPdfGenerator;
}
    /**
     * Retrieves the current authenticated patient's account.
     *
     * @return the Account object of the authenticated patient.
     */
    public Account getAccount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        AccountDetails user = (AccountDetails) userDetailsService.loadUserByUsername(currentPrincipalName);
        Account patientAccount = user.getAccount();
        return patientAccount;
    }
    /**
     * Gets the details of the currently authenticated patient.
     *
     * @return ResponseEntity containing the patient's details.
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping("patient/getDetails")
    public ResponseEntity<Object> getPatientInfo() {
        RequestRes res = new RequestRes();
        Account patientAccount = getAccount();
        Patient patient = (Patient) patientAccount.getPerson();
        res.setStatusCode(200);
        res.setPatient(patient);
        return ResponseEntity.ok(res);
    }
    /**
     * Fetches and returns a paginated list of prescriptions for the patient, sorted by a specified field.
     *
     * @param sort the field to sort by (optional).
     * @param size the number of prescriptions per page (optional).
     * @param page the current page number (optional).
     * @return ResponseEntity containing a paginated and sorted list of prescriptions.
     */
    @GetMapping("/patient/prescriptionsSorted")
    public ResponseEntity<Object> getSortedPagePrescriptions(
            @RequestParam("sortField") @Nullable String sort,
            @RequestParam("size") @Nullable Integer size,
            @RequestParam("page") @Nullable Integer page) {

        RequestRes res = new RequestRes();

        // Fetch patient information
        Patient patient = patientRepository.findById(getAccount().getPerson().getId()).orElse(null);
        if (patient == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Patient not found");
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
     * Retrieves detailed information about the currently authenticated patient.
     *
     * @return ResponseEntity containing the patient's detailed information.
     */
    @GetMapping("/patient/details")
    public ResponseEntity<Object> getPatientDetails() {
        RequestRes res = new RequestRes();
        PatientDetailsDTO patientDetailsDTO = new PatientDetailsDTO();
        Patient patient =patientRepository.findById(getAccount().getPerson().getId()).get();
        res.setStatusCode(200);
        res.setPatient(patient);

        return ResponseEntity.status(200).body(res);

    }
    /**
     * Retrieves a specific prescription for the patient by its ID.
     *
     * @param id the ID of the prescription to retrieve.
     * @return ResponseEntity containing the prescription details if found,
     *         or a 404 response if not found.
     */
    @GetMapping("/patient/prescription/{id}")
    public ResponseEntity<Object> getPrescription(@PathVariable("id")Long id) {
        RequestRes requestRes = new RequestRes();
        Optional<Prescription> prescription = prescriptionRepository.findById(id);
        Account patientAccount = getAccount();
        Patient patient = (Patient) patientAccount.getPerson();
        if(prescription.isEmpty()) {
            requestRes.setError("404");
            requestRes.setMessage("Prescription Not Found");
            requestRes.setStatusCode(404);
            return ResponseEntity.status(404).body(requestRes);
        } else if(prescription.get().getPatient().getId() != patient.getId()) {
            requestRes.setStatusCode(403);
            requestRes.setMessage("Prescription Does Not Belong to the patient!");
            return ResponseEntity.status(403).body(requestRes);
        } else {

            requestRes.setStatusCode(200);
            requestRes.setPrescription(prescription.get());
            requestRes.setPhysicianDTO(new PhysicianDTO(prescription.get().getPhysician()));
            requestRes.setPatientDTO(new PatientDTO(patient));
            return ResponseEntity.status(200).body(requestRes);
        }
    }
    /**
     * Generates a PDF for the specified prescription and allows the patient to download it.
     *
     * @param response the HttpServletResponse to set headers for the download.
     * @param id the ID of the prescription to download.
     * @return ResponseEntity containing the generated PDF file as a downloadable resource.
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @RequestMapping(value = "/patient/downloadPrescription/{id}", method = RequestMethod.GET, produces =   { MediaType.APPLICATION_OCTET_STREAM_VALUE})
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
    Patient patient = (Patient) getAccount().getPerson();

        if(prescription.getPatient().getId() != prescription.getPatient().getId()) {
            res.setMessage("Prescription does not belong to the patient!");
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
