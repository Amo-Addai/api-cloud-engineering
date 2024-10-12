package com.example.samples.repositories;

import com.example.samples.models.Company;
import com.example.samples.models.User;

import java.util.List;

public interface CompanyRepository {

    Company save(Company user);
    Company findById(Long id);
    List<Company> findAll();

}
