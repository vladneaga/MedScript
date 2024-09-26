package group11.medScriptAPI.DTO;

import group11.medScriptAPI.entity.Person;

import java.util.Date;

public class PersonDTO {
    private String firstname;
    private String secondName;
    private String address;
    private Date birthDate;

    public PersonDTO(Person person) {
        this.firstname = person.getFirstname();
        this.secondName = person.getSecondName();
        this.address = person.getAddress();
        this.birthDate = person.getBirthDate();
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
}
