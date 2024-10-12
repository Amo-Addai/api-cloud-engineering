package com.example.samples.models;

import jakarta.persistence.*;

import java.util.Date;


@Entity
@Table(name = "\"user\"")
/**
 H2 is confused by the word user in your SQL command, as it’s a reserved keyword.
 When H2 sees USER, it thinks you're referring to its internal concept of a user, not your table named user.
 Modify the table name by escaping it with double quotes.
 Tells H2 (and most SQL databases) to treat user as a literal identifier rather than a keyword
 */
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String username;
    private String first_name;
    private String last_name;
    private String email;
    private String address;
    private Long company_id;
    private Date record_date;

    public User() {} // empty constructor for spring - h2-database @Entity relationship

    // custom constructor for db-seeding (not optimal if not required)
    public User(
            String username,
            String first_name,
            String last_name,
            String email,
            String address,
            Long company_id,
            Date record_date
    ) {
        this.username = username;
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.address = address;
        this.company_id = company_id;
        this.record_date = record_date;
    }

    public Date getRecord_date() {
        return record_date;
    }

    public void setRecord_date(Date record_date) {
        this.record_date = record_date;
    }

    public Long getCompany_id() {
        return company_id;
    }

    public void setCompany_id(Long company_id) {
        this.company_id = company_id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
