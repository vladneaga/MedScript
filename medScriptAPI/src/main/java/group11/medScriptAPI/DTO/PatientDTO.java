package group11.medScriptAPI.DTO;

import group11.medScriptAPI.entity.Person;

public class PatientDTO {
    private Person person;

    public PatientDTO(Person person) {
        this.person = person;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
}
