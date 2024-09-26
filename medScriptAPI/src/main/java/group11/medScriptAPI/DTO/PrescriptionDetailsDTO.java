package group11.medScriptAPI.DTO;





import group11.medScriptAPI.entity.Patient;
import group11.medScriptAPI.entity.Physician;
import group11.medScriptAPI.entity.Prescription;

import java.util.Date;

public class PrescriptionDetailsDTO {
    Physician physician;
    Prescription prescription;
    Patient patient;

    public PrescriptionDetailsDTO(Physician physician, Prescription prescription, Patient patient) {
        this.physician = physician;
        this.prescription = prescription;
        this.patient = patient;
    }
    public String getText() {
        return prescription.getText();
    }
    public String getMedication() {
        return prescription.getMedication();
    }
    public String getTotalGrammage() {
        return prescription.getTotalGrammage();
    }
    public Date getCreationDate() {
        return prescription.getCreationDate();
    }
    public Date getExpirationDate() {
        return prescription.getExpirationDate();
    }
    public String getPhysicianFirstname() {
        return physician.getFirstname();
    }
    public String getSecondName() {
        return physician.getSecondName();
    }
    public String getInstitutionName() {
        return physician.getInstitution().getName();
    }

    public Physician getPhysician() {
        return physician;
    }

    public void setPhysician(Physician physician) {
        this.physician = physician;
    }

    public Prescription getPrescription() {
        return prescription;
    }

    public void setPrescription(Prescription prescription) {
        this.prescription = prescription;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }
    public Long getPrescriptionId() {
        return this.prescription.getId();
    }
}
