package group11.medScriptAPI.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a Patient entity that extends the Person class.
 * This class contains information about the patient's prescriptions,
 * physicians, and insurance.
 */
@Entity
public class Patient extends Person implements Serializable {



    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    private Set<Prescription> prescriptions = new HashSet<>();

    @ManyToMany(cascade = CascadeType.ALL)
    @JsonIgnore
    @JoinTable(
        name="PHY_PAT",
        joinColumns=@JoinColumn(name="PAT_ID", referencedColumnName="ID"),
        inverseJoinColumns=@JoinColumn(name="PHY_ID", referencedColumnName="ID")
    )
    private Set<Physician> physicians;

    @OneToOne(mappedBy = "patient")
    Insurance insurance;

    public Patient() {
    }
    public Patient(String address, Insurance insurance, Date birthDate) {
        super(address, birthDate);
        setInsurance(insurance);


    }
    public Set<Prescription> getPrescriptions() {
        return prescriptions;
    }

    public void setPrescriptions(Set<Prescription> prescriptions) {
        this.prescriptions = prescriptions;
    }

    public Set<Physician> getPhysicians() {
        return physicians;
    }

    public void setPhysicians(Set<Physician> physicians) {
        this.physicians = physicians;
    }



    public Insurance getInsurance() {
        return insurance;
    }

    public void setInsurance(Insurance insurance) {
        this.insurance = insurance;
    }

    public void addPrescription(Prescription prescription) {
        this.prescriptions.add(prescription);
    }
}

