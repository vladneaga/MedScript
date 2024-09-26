package group11.medScriptAPI.DTO;

import group11.medScriptAPI.entity.Physician;
import jakarta.persistence.*;

import java.util.Date;

public class PhysicianDTO  {




    public PhysicianDTO(Physician physician) {
        this.address = physician.getAddress();
        this.birthDate = physician.getBirthDate();
        this.firstname = physician.getFirstname();
        this.secondName = physician.getSecondName();
    }


    private String address;

    private String firstname;

    private String secondName;

    @Temporal(TemporalType.DATE)
    private Date birthDate;

    public String getAddress() {
        return address;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getSecondName() {
        return secondName;
    }

    public Date getBirthDate() {
        return birthDate;
    }
}
