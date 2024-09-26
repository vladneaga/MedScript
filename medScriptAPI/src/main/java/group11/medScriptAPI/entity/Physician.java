package group11.medScriptAPI.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.io.File;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a Physician entity, extending the Person class.
 * A Physician can prescribe medications to patients and is associated with an institution and a creator admin.
 */
@Entity
public class Physician extends Person implements Serializable {


  /*  @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;*/

    @JsonIgnore
    @OneToMany(mappedBy = "physician", cascade = CascadeType.ALL)
    private Set<Prescription> prescriptions = new HashSet<>();


    @ManyToMany(mappedBy = "physicians",cascade = CascadeType.ALL)
    private Set<Patient> patients = new HashSet<>();

    private Institution institution;
    private Admin creatorAdmin;

    @Column(nullable = true)
    private File signatureFile;

    public File getSignatureFile() {
        return signatureFile;
    }

    public void setSignatureFile(File signatureFile) {
        this.signatureFile = signatureFile;
    }

    public Set<Prescription> getPrescriptions() {
        return prescriptions;
    }

    public void setPrescriptions(Set<Prescription> prescriptions) {
        this.prescriptions = prescriptions;
    }

    public Set<Patient> getPatients() {
        return patients;
    }

    public void setPatients(Set<Patient> patients) {
        this.patients = patients;
    }

    public Institution getInstitution() {
        return institution;
    }

    public void setInstitution(Institution institution) {
        this.institution = institution;
    }

    public Admin getCreatorAdmin() {
        return creatorAdmin;
    }

    public void setCreatorAdmin(Admin creatorAdmin) {
        this.creatorAdmin = creatorAdmin;
    }



    public Physician() {
    }
}
