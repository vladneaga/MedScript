package group11.medScriptAPI.DTO;


import group11.medScriptAPI.entity.Account;
import group11.medScriptAPI.entity.Physician;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class PhysicianAccountDTO {
    private String username;
    private boolean unblocked;
    private String password;
    private String email;
    private String phoneNumber;
    private String address;
    private String firstname;

    private String secondName;
    Account account;
    Physician physician;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date birthDate;

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

    public String getSecondName() {
        return secondName;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
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

    public Account getAccount() {

        if(account == null) {
            account = new Account();
        }

        account.setUsername(username);
        account.setPassword(password);
        account.setPhoneNumber(phoneNumber);
        account.setEmail(email);
        account.setUnblocked(unblocked);
        return account;
    }
    public Physician getPhysician() {
        if(physician == null) {
            physician = new Physician();
        }

        physician.setBirthDate(birthDate);
        physician.setFirstname(firstname);
        physician.setSecondName(secondName);
        physician.setAddress(address);
        return physician;
    }
}
