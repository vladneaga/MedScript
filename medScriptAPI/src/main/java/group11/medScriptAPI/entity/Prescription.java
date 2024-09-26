package group11.medScriptAPI.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * Represents a Prescription entity, which contains information about prescribed medications
 * by a physician to a patient, along with creation and expiration dates.
 */
@Entity
public class Prescription implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String text;
    private String medication;
    private String totalGrammage;
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date expirationDate;
    @JsonIgnore
    @ManyToOne(cascade = CascadeType.ALL)
    private Physician physician;
    @JsonIgnore
    @ManyToOne(cascade = CascadeType.ALL)
    private Patient patient;
    public Prescription() {
    }

    public Long getId() {
        return id;
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Physician getPhysician() {
        return physician;
    }

    public void setPhysician(Physician physician) {
        this.physician = physician;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public String getMedication() {
        return medication;
    }

    public void setMedication(String medication) {
        this.medication = medication;
    }

    public String getTotalGrammage() {
        return totalGrammage;
    }


    @Override
    public String toString() {
        return "Prescription{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", medication='" + medication + '\'' +
                ", totalGrammage='" + totalGrammage + '\'' +
                ", creationDate=" + creationDate +
                ", expirationDate=" + expirationDate +
                ", physician=" + physician +
                ", patient=" + patient +
                '}';
    }

    public void setTotalGrammage(String totalGrammage) {
        this.totalGrammage = totalGrammage;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }
}
