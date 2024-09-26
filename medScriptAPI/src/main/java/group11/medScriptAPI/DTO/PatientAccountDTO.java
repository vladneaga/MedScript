package group11.medScriptAPI.DTO;


import group11.medScriptAPI.entity.Account;
import group11.medScriptAPI.entity.Insurance;
import group11.medScriptAPI.entity.Patient;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class PatientAccountDTO {
    private String username;
    private boolean unblocked;
    private String email;
    private String password;
    private String phoneNumber;
    private String address;
    private String firstname;

    private String secondName;
    private String insuranceNumber;
    private boolean isUnblocked;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date birthDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;
    Patient patient;
    Account account;
    Insurance insurance;
    public boolean getIsUnblocked() {
        return this.isUnblocked;
    }
    public void setIsUnBlocked(boolean isUnBlocked) {
        this.isUnblocked = isUnBlocked;
    }
    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getInsuranceNumber() {
        return insuranceNumber;
    }

    public void setInsuranceNumber(String insuranceNumber) {
        this.insuranceNumber = insuranceNumber;
    }

    public Insurance getInsurance() {
        return insurance;
    }

    public void setInsurance(Insurance insurance) {
        this.insurance = insurance;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isUnblocked() {
        return unblocked;
    }

    public void setUnblocked(boolean unblocked) {
        this.unblocked = unblocked;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public Patient getPatient() {
        if(patient == null) {
            patient = new Patient();
        }
        patient.setInsurance(insurance);
        patient.setBirthDate(birthDate);
        patient.setFirstname(firstname);
        patient.setSecondName(secondName);
        patient.setAddress(address);
        return patient;
    }

    public Account getAccount() {
        if(account == null) {
            account = new Account();
        }
        account.setCreationDate(creationDate);
        account.setUsername(username);
        account.setPassword(password);
        account.setPhoneNumber(phoneNumber);
        account.setEmail(email);
        account.setUnblocked(unblocked);
        return account;
    }
}
