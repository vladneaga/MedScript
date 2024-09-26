package group11.medScriptAPI.DTO;



import group11.medScriptAPI.entity.Prescription;

import java.util.Date;
import java.util.List;

public class PatientDetailsDTO {
    private String firstname;
    private String secondname;
    private String address;
    private Date birthDate;
    private String krankenkassenName;
    private String insuranceNumber;
    private List<Prescription> prescriptions;
    private String physicianName;

    // Getters and setters

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getSecondname() {
        return secondname;
    }

    public void setSecondname(String secondname) {
        this.secondname = secondname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getKrankenkassenName() {
        return krankenkassenName;
    }

    public void setKrankenkassenName(String krankenkassenName) {
        this.krankenkassenName = krankenkassenName;
    }

    public String getInsuranceNumber() {
        return insuranceNumber;
    }

    public void setInsuranceNumber(String insuranceNumber) {
        this.insuranceNumber = insuranceNumber;
    }

    public List<Prescription> getPrescriptions() {
        return prescriptions;
    }

    public void setPrescriptions(List<Prescription> prescriptions) {
        this.prescriptions = prescriptions;
    }

    public String getPhysicianName() {
        return physicianName;
    }

    public void setPhysicianName(String physicianName) {
        this.physicianName = physicianName;
    }
}