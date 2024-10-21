package coms309.people;

public class SpecialPerson extends Person {
    int specialID;

    SpecialPerson(String firstName, String lastName, String address, String telephone, int specialID){
        super(firstName, lastName, address, telephone);
        this.specialID = specialID;
    }

    public int getSpecialID() {
        return this.specialID;
    }
}
