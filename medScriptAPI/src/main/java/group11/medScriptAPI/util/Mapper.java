package group11.medScriptAPI.util;

import group11.medScriptAPI.DTO.AdminAccountDTO;
import group11.medScriptAPI.DTO.PatientAccountDTO;
import group11.medScriptAPI.DTO.PhysicianAccountDTO;
import group11.medScriptAPI.DTO.RequestRes;
import group11.medScriptAPI.entity.*;
import group11.medScriptAPI.repository.AdminRepository;
import group11.medScriptAPI.repository.InstitutionRepository;
import group11.medScriptAPI.repository.InsuranceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
/**
 * Mapper class for transforming RequestRes objects into various account and entity DTOs.
 * This class is responsible for mapping input data to domain models for patients, physicians, and administrators.
 */
@Component
public class Mapper {
    @Autowired
    InsuranceRepository insuranceRepository;
    @Autowired
    InstitutionRepository institutionRepository;
    @Autowired
    AdminRepository adminRepository;
    /**
     * Maps a RequestRes object to a PatientAccountDTO.
     *
     * @param req the RequestRes object containing the data to map
     * @return a PatientAccountDTO populated with data from the RequestRes object
     */
    public PatientAccountDTO mapToPatientAccountDTO(RequestRes req) {
        PatientAccountDTO patientAccountDTO = new PatientAccountDTO();
        patientAccountDTO.setUsername(req.getUsername());
        patientAccountDTO.setPassword(req.getPassword());
        patientAccountDTO.setFirstname(req.getFirstname());
        patientAccountDTO.setSecondName(req.getSecondname());
        patientAccountDTO.setAddress(req.getAddress());
        patientAccountDTO.setEmail(req.getEmail());
        patientAccountDTO.setBirthDate(req.getBirthDate());
        patientAccountDTO.setInsuranceNumber(req.getInsuranceNumber());
        patientAccountDTO.setPhoneNumber(req.getPhoneNumber());
        patientAccountDTO.setUnblocked(req.getIsUnblocked() != null ? req.getIsUnblocked() : true);
        return patientAccountDTO;
    }
    /**
     * Maps a RequestRes object to a PhysicianAccountDTO.
     *
     * @param req the RequestRes object containing the data to map
     * @return a PhysicianAccountDTO populated with data from the RequestRes object
     */
    public PhysicianAccountDTO mapToPhysicianAccountDTO(RequestRes req) {
        PhysicianAccountDTO physicianAccountDTO = new PhysicianAccountDTO();
        physicianAccountDTO.setUsername(req.getUsername());
        physicianAccountDTO.setPassword(req.getPassword());
        physicianAccountDTO.setFirstname(req.getFirstname());
        physicianAccountDTO.setSecondName(req.getSecondname());
        physicianAccountDTO.setAddress(req.getAddress());
        physicianAccountDTO.setEmail(req.getEmail());
        physicianAccountDTO.setUnblocked(req.getIsUnblocked() != null ? req.getIsUnblocked() : true);
        physicianAccountDTO.setBirthDate(req.getBirthDate());
        physicianAccountDTO.setPhoneNumber(req.getPhoneNumber());
        return physicianAccountDTO;
    }
    /**
     * Maps a RequestRes object to an AdminAccountDTO.
     *
     * @param req the RequestRes object containing the data to map
     * @return an AdminAccountDTO populated with data from the RequestRes object
     */
    public AdminAccountDTO mapToAdminAccountDTO(RequestRes req) {
        AdminAccountDTO adminAccountDTO = new AdminAccountDTO();
        adminAccountDTO.setUsername(req.getUsername());
        adminAccountDTO.setPassword(req.getPassword());
        adminAccountDTO.setFirstname(req.getFirstname());
        adminAccountDTO.setSecondName(req.getSecondname());
        adminAccountDTO.setAddress(req.getAddress());
        adminAccountDTO.setEmail(req.getEmail());
        adminAccountDTO.setUnblocked(req.getIsUnblocked());
        adminAccountDTO.setBirthDate(req.getBirthDate());
        adminAccountDTO.setPhoneNumber(req.getPhoneNumber());
        return adminAccountDTO;
    }
    /**
     * Maps a RequestRes object to an Account entity.
     *
     * @param req the RequestRes object containing the data to map
     * @return an Account entity populated with data from the RequestRes object
     */
    public Account mapToAccount(RequestRes req) {
        Account account = new Account();
        account.setUsername(req.getUsername());
        account.setPassword(req.getPassword());
        account.setPhoneNumber(req.getPhoneNumber());
        account.setEmail(req.getEmail());
        return account;
    }
    /**
     * Maps a RequestRes object to a Patient entity.
     *
     * @param req the RequestRes object containing the data to map
     * @return a Patient entity populated with data from the RequestRes object
     * @throws NullPointerException if the insurance number provided is not found
     */
    public Patient mapToPatient(RequestRes req) {
        Patient patient = new Patient();
        patient.setFirstname(req.getFirstname());
        patient.setSecondName(req.getSecondname());
        patient.setAddress(req.getAddress());
        patient.setBirthDate(req.getBirthDate());
        Optional<Insurance> insuranceOptional =  insuranceRepository.findByNumber(req.getInsuranceNumber()).stream().findFirst();
        Insurance insurance;
        if(insuranceOptional.isEmpty()) {
            throw new NullPointerException("Inusrance not fount!!!");
        } else {
          insurance =insuranceRepository.findByNumber(req.getInsuranceNumber()).stream().findFirst().get();
        }
        patient.setInsurance(insurance);
        insurance.setPatient(patient);

        return patient;
    }

    /**
     * Maps a RequestRes object to a Physician entity.
     *
     * @param req the RequestRes object containing the data to map
     * @return a Physician entity populated with data from the RequestRes object
     */
    public Physician mapToPhysician(RequestRes req) {
        Physician physician = new Physician();
        physician.setCreatorAdmin(adminRepository.findById(req.getCreatorAdminId()).stream().findFirst().get());
        physician.setFirstname(req.getFirstname());
        physician.setSecondName(req.getSecondname());
        physician.setAddress(req.getAddress());
        physician.setBirthDate(req.getBirthDate());
        physician.setInstitution(institutionRepository.findById(req.getInstitutionId()).stream().findFirst().get());
        return physician;
    }
    /**
     * Maps a RequestRes object to an Admin entity.
     *
     * @param req the RequestRes object containing the data to map
     * @return an Admin entity populated with data from the RequestRes object
     */
    public Admin mapToAdmin(RequestRes req) {
        Admin admin = new Admin();
        admin.setCreatorAdmin(adminRepository.findById(req.getCreatorAdminId()).stream().findFirst().get());
        admin.setInstitution(institutionRepository.findById(req.getInstitutionId()).stream().findFirst().get());
        admin.setAddress(req.getAddress());
        admin.setFirstname(req.getFirstname());
        admin.setSecondName(req.getSecondname());
        admin.setBirthDate(req.getBirthDate());
        return admin;
    }
}
