package group11.medScriptAPI.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
/**
 * Represents an Admin entity in the system.
 * This class extends the Person class and contains details specific to administrators,
 * including their associated institution and the physicians they manage.
 */
@Entity
public class Admin extends Person implements Serializable {
    private Institution institution;

    @JsonIgnore
    @OneToMany(mappedBy = "creatorAdmin")
    private Set<Physician> physicians;
    @JsonIgnore
    private Admin creatorAdmin;
    @JsonIgnore
    @OneToMany(mappedBy = "creatorAdmin")
    private Set<Admin> admins;

    public Admin() {
        this.physicians = new HashSet<>();
        this.admins = new HashSet<>();
    }

    public Institution getInstitution() {
        return institution;
    }

    public void setInstitution(Institution institution) {
        this.institution = institution;
    }

    public Set<Physician> getPhysicians() {
        return physicians;
    }

    public void setPhysicians(Set<Physician> physicians) {
        this.physicians = physicians;
    }

    public Admin getCreatorAdmin() {
        return creatorAdmin;
    }

    public void setCreatorAdmin(Admin creatorAdmin) {
        this.creatorAdmin = creatorAdmin;
    }

    public Set<Admin> getAdmins() {
        return admins;
    }

    public void setAdmins(Set<Admin> admins) {
        this.admins = admins;
    }
}
