package coms309.book;

import org.springframework.web.bind.annotation.*;


import java.util.HashMap;

/**
 * Controller used to showcase Create and Read from a LIST
 *
 * @author Vivek Bengre
 */

@RestController
@RequestMapping("/krclark/coms309")
public class BookController {

    // Note that there is only ONE instance of PeopleController in 
    // Springboot system.
    HashMap<String, Book> BookList = new  HashMap<>();

    //CRUDL (create/read/update/delete/list)
    // use POST, GET, PUT, DELETE, GET methods for CRUDL

    // THIS IS THE LIST OPERATION
    // gets all the people in the list and returns it in JSON format
    // This controller takes no input. 
    // Springboot automatically converts the list to JSON format 
    // in this case because of @ResponseBody
    // Note: To LIST, we use the GET method
    @GetMapping("/book")
    public @ResponseBody HashMap<String,Book> getAllPersons() {
        return BookList;
    }

    // THIS IS THE CREATE OPERATION
    // springboot automatically converts JSON input into a person object and 
    // the method below enters it into the list.
    // It returns a string message in THIS example.
    // in this case because of @ResponseBody
    // Note: To CREATE we use POST method
    @PostMapping("/book")
    public @ResponseBody String createPerson(@RequestBody Book book) {
        System.out.println(book);
        BookList.put(book.getTitle(), book);
        return "New book "+ book.getTitle() + " Saved";
    }

    // THIS IS THE READ OPERATION
    // Springboot gets the PATHVARIABLE from the URL
    // We extract the person from the HashMap.
    // springboot automatically converts Person to JSON format when we return it
    // in this case because of @ResponseBody
    // Note: To READ we use GET method
    @GetMapping("/book/{title}")
    public @ResponseBody Book getPerson(@PathVariable String title) {
        Book b = BookList.get(title);
        return b;
    }

    // THIS IS THE UPDATE OPERATION
    // We extract the person from the HashMap and modify it.
    // Springboot automatically converts the Person to JSON format
    // Springboot gets the PATHVARIABLE from the URL
    // Here we are returning what we sent to the method
    // in this case because of @ResponseBody
    // Note: To UPDATE we use PUT method
    @PutMapping("/book/{title}")
    public @ResponseBody Book updatePerson(@PathVariable String title, @RequestBody Book b) {
        BookList.replace(title, b);
        return BookList.get(title);
    }

    // THIS IS THE DELETE OPERATION
    // Springboot gets the PATHVARIABLE from the URL
    // We return the entire list -- converted to JSON
    // in this case because of @ResponseBody
    // Note: To DELETE we use delete method
    
    @DeleteMapping("/book/{title}")
    public @ResponseBody HashMap<String, Book> deletePerson(@PathVariable String title) {
        BookList.remove(title);
        return BookList;
    }
}

