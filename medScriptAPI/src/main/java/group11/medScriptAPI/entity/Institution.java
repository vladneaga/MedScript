package group11.medScriptAPI.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents an Institution entity in the system.
 * This class contains details about an institution, including its name, location,
 * and the associated physicians and administrators.
 */
@Entity
@Table
public class Institution  implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;
    @Column(name = "city")
    private String city;
    @Column(name = "country")
    private String country;

    @OneToMany(mappedBy = "institution")
    @JsonIgnore
    private Set<Physician> physicians;
    @OneToMany(mappedBy = "institution")
    @JsonIgnore
    private Set<Admin> admins;

    public Set<Physician> getPhysicians() {
        return physicians;
    }

    public void setPhysicians(Set<Physician> physicians) {
        this.physicians = physicians;
    }

    public Set<Admin> getAdmins() {
        return admins;
    }

    public void setAdmins(Set<Admin> admins) {
        this.admins = admins;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }


    public Institution(String name, String city, String country) {
        this();
        this.name = name;
        this.city = city;
        this.country = country;
    }
    public Institution() {
        this.admins = new HashSet<>();
        this.physicians = new HashSet<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
