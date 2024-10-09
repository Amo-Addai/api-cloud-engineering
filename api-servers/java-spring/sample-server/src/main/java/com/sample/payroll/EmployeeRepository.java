package com.sample.payroll;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Employee save(Employee employee);
    List<Employee> findAll();
    Employee findById(Long id);

}