package com.example.samples.controllers;

import com.example.samples.models.Company;
import com.example.samples.services.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    @GetMapping(path="/companies")
    public List<Company> getAll() {
        return companyService.findAll();
    }

    @PostMapping(path="/companies")
    public Company save(@RequestBody Company company) {
        return companyService.save(company);
    }

}
