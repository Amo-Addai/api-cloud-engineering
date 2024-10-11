package com.example.demo;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

@RestController
public class SimpleController {

    private final DBUser db = new DBUser();

    // GET Requests

    @RequestMapping(
            value="/hi", // path="/hi",
            method=RequestMethod.GET,
            consumes="application/json", produces="application/json"
    )
    @CrossOrigin(origins="http://localhost:8080") // todo: env-var
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
    @GetMapping("/user")
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

    @PostMapping(path="/users/add", consumes=MediaType.APPLICATION_JSON_VALUE) // can't produce app/json if handler returns bool
    @ResponseStatus(HttpStatus.CREATED) // POST 201 Created
    public boolean addUser(@RequestBody User user) {
        return db.addUser(user);
    }

    // PUT Requests

    @PutMapping("/users/{id}")
    // TODO: Causing Internal Server Error
    @ResponseStatus(HttpStatus.OK) // POST 201 Created
    public User replaceUser(@PathVariable int id, @RequestBody User user) {
        return db.replaceUser(id, user);
    }

    // PATCH Requests

    @PatchMapping("/users/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public User updateUser(@PathVariable int id, @RequestBody User user) {
        return db.updateUser(id, user);
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

    // ALT Requests // TODO: Test

    // File Uploads
    @RequestMapping(
            path="/users/upload",
            method=RequestMethod.POST,
            consumes=MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public String fileUpload(
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        File convertFile = new File(
                "/var/tmp/"
                        + file.getOriginalFilename()
        );
        convertFile.createNewFile(); // returns new File value
        // todo: confirm new file also created in-place of convertFile
        FileOutputStream fout = new FileOutputStream(convertFile);
        fout.write(file.getBytes());
        fout.close();
        return "File uploaded successfully";
    }

    // File Downloads
    @RequestMapping(
            path="/users/download",
            method=RequestMethod.GET
    )
    public ResponseEntity<Object> downloadFile() throws IOException {
        String filename = "/var/tmp/mysql.png"; // todo: ensure file exists
        File file = new File(filename);
        if (file.exists()) {
            InputStreamResource resource =
                    new InputStreamResource(
                            new FileInputStream(file)
                    );

            // now return file as ResponseEntity, for download
            // set up headers 1st
            HttpHeaders headers = new HttpHeaders();
            headers.add(
                    "Content-Disposition",
                    String.format("attachment;filename=\"%s\"",
                            file.getName())
            );
            headers.add(
                    "Cache-Control",
                    "no-cache, no-store, must-revalidate"
            );
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");

            // then, ResponseEntity
            ResponseEntity<Object> response =
                    ResponseEntity.ok()
                            .headers(headers)
                            .contentLength(file.length())
                            .contentType(
                                    MediaType.parseMediaType(
                                            "application/txt"
                                    )
                            )
                            .body(resource);
            return response;
        }
        return null;
    }

}
