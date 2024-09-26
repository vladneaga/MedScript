package group11.medScriptAPI.entity;


import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;
/**
 * Represents an abstract Person entity with common attributes shared among all person types.
 * This class includes personal details such as name, address, and birth date.
 * It is the base class for specific person types like Patient and Admin.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Person implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="address",
            nullable = false)
    private String address;
    @Column(name="firstname",
            nullable = false)
    private String firstname;
    @Column(name="second_name",
            nullable = false)
    private String secondName;

    @Column(name="birth_date",
            nullable = false)
    @Temporal(TemporalType.DATE)
    private Date birthDate;

    @OneToOne(mappedBy = "person")
    private Account account;

    public Person() {
    }
    public String getFullName() {
        return secondName + " " + firstname;
    }
    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Person(String address, Date birthDate) {
        this.address = address;

        this.birthDate = birthDate;
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

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}
