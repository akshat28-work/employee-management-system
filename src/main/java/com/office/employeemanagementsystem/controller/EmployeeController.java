package com.office.employeemanagementsystem.controller;

import com.office.employeemanagementsystem.entity.Employee;
import com.office.employeemanagementsystem.repository.EmployeeRepository;
import com.office.employeemanagementsystem.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employees")
public class EmployeeController {
  @Autowired
  private EmployeeService employeeService;

  @GetMapping
  public ResponseEntity<List<Employee>> findAll() {
    return new ResponseEntity<>(employeeService.findAllEmployees(), HttpStatus.OK);
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> findById(@PathVariable Long id) {
    return new ResponseEntity<>(employeeService.findEmployeeById(id), HttpStatus.OK);
  }

  @PostMapping
  public ResponseEntity<Employee> save(@Valid @RequestBody Employee employee) {
    return new ResponseEntity<>(employeeService.saveEmployee(employee), HttpStatus.CREATED);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<String> delete(@PathVariable Long id) {
    employeeService.deleteEmployeeById(id);
    return new ResponseEntity<>("Deleted successfully", HttpStatus.OK);
  }

  @GetMapping("/department/{deptId}")
  public ResponseEntity<List<Employee>> findByDepartmentId(@Valid @PathVariable Long deptId) {
    return new ResponseEntity<>(employeeService.getEmployeesByDept(deptId), HttpStatus.OK);
  }

  @GetMapping("/project/{projId}")
  public ResponseEntity<List<Employee>> getByProject(@Valid @PathVariable Long projId) {
    return new ResponseEntity<>(employeeService.getEmployeesByProject(projId), HttpStatus.OK);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Employee> update(@PathVariable Long id, @Valid @RequestBody Employee employee) {
    return new ResponseEntity<>(employeeService.updateEmployee(id, employee), HttpStatus.OK);
  }
}
