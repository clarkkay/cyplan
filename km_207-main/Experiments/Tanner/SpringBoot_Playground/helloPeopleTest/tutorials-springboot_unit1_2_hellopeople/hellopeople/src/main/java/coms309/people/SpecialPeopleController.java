package coms309.people;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
@RestController
public class SpecialPeopleController {
    HashMap<String, SpecialPerson> specialPeopleList = new  HashMap<>();
    @GetMapping("/special")
    public @ResponseBody HashMap<String,SpecialPerson> getAllPersons() {
        return specialPeopleList;
    }
    @PostMapping("/special")
    public @ResponseBody String createPerson(@RequestBody SpecialPerson person) {
        System.out.println(person);
        specialPeopleList.put(person.getFirstName(), person);
        return "New SPECIAL person "+ person.getFirstName() + " Saved";
    }

    @GetMapping("/special/{firstName}")
    public @ResponseBody Person getPerson(@PathVariable String firstName) {
        SpecialPerson p = specialPeopleList.get(firstName);
        return p;
    }

    @DeleteMapping("/special/{firstName}")
    public @ResponseBody HashMap<String, SpecialPerson> deletePerson(@PathVariable String firstName) {
        specialPeopleList.remove(firstName);
        return specialPeopleList;
    }

    //SWITCH A PERSON TO SPECIAL PERSON
}
