package com.example.demo;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class SimpleController {

    private final DBUser db = new DBUser();

    // GET Requests

    @RequestMapping(
            path="/hi", method=RequestMethod.GET,
            consumes="application/json", produces="application/json"
    )
    public String hi() {
        return "Hello, from server";
    }

    // GET http://localhost:8080/search?q=StringValue
    @GetMapping("/search")
    public String searchWord(@RequestParam String q) {
        DataStore db = new DataStore();
        return db.getFromDB(q);
    }

    // http://localhost:8080/users
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return db.getAllUsers();
    }

    // http://localhost:8080/users?q=username
    @GetMapping("/users")
    public User getUser(@RequestParam String q) {
        return db.getUserByName(q);
    }

    // http://localhost:8080/users/{id}
    @GetMapping("/users/{id}") // * /{id} required both in GetMapping, & as PathVariable arg
    public User getUser(@PathVariable int id) {
        return db.getUser(id);
    }

    // POST Requests

    // POST http://localhost:8080/users with RequestBody - User
    @PostMapping("/users")
    public User createUser(@RequestBody User user) {
        return db.createUser(user);
    }

    @PostMapping(path="/users/add", produces=MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED) // POST 201 Created
    public boolean addUser(@RequestBody User user) {
        return db.addUser(user);
    }

    // DELETE Requests

    @DeleteMapping("/users/{id}")
    public boolean removeUser(@PathVariable int id) {
        return db.deleteUser(id);
    }

    // Extra Request Samples

    @PostMapping("/samples")
    public boolean sample(@RequestBody User u) throws Exception {
        if (u == null) throw new Exception("Error");
        return db.addUser(u);
    }

    @GetMapping("/samples/{id}")
    public ResponseEntity<User> getSampleUser(@PathVariable int id) {
        User user = db.getUser(id);
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.put("header-key", Collections.singletonList("header-value"));

        return new ResponseEntity<User>( // return http-response with additional custom headers & status
                user, headers, HttpStatus.ACCEPTED
        );
    }

}
