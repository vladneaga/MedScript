package group11.medScriptAPI.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;
/**
 * Entity class representing an Account.
 * This class contains account-related information such as username, password, email, and role.
 */
    @Entity
    @Table(name = "Account",
    uniqueConstraints = {
            @UniqueConstraint(name="email_unique", columnNames = "email"),
            @UniqueConstraint(name="phone_unique", columnNames = "phoneNumber")
    })
    public class Account implements Serializable {
    @Id
    @SequenceGenerator(
            name="account_sequence",
            sequenceName = "account_sequence",
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
    generator = "account_sequence")
    @Column(name = "id",
            updatable = false)
    private Long id;

    @Column(name = "username",
    nullable = false,
    unique = true)
    private String username;

    @Column(name = "isUnblocked",
            nullable = false)
    private boolean unblocked;


    @Column(name = "password",
            nullable = false)
    private String password;

    @Column(name="role",
            nullable = false)
    private String role;
    @Column(name="creation_date",
            nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;



    @Column(name="email",nullable = false)
    private String email;

    @Column(name="phone_number",
            nullable = false,
            unique = true)
    private String phoneNumber;
    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "person_id",
    referencedColumnName = "id")
    private Person person;

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    // Constructor needed for default
    public Account() {
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public boolean getUnblocked() {
        return unblocked;
    }

    public void setUnblocked(boolean unblocked) {
        this.unblocked = unblocked;
    }

}
