package group11.medScriptAPI.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

/**
 * Represents an Insurance entity associated with a patient.
 * This class contains details about the insurance company and the insurance number.
 */
@Entity
@Table
public class Insurance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "insurance_company")
    private String insurance_company;
    @Column(name = "number",
            nullable = false,
            unique = true)
        private String number;
    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "patient_id",
            referencedColumnName = "id")
    private Patient patient;

    public Long getId() {
        return id;
    }

    public Insurance(String number) {
        this.number = number;
    }

    public Insurance() {
    }

    public String getInsurance_company() {
        return insurance_company;
    }

    public void setInsurance_company(String insurance_company) {
        this.insurance_company = insurance_company;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
