package com.office.employeemanagementsystem.controller;

import com.office.employeemanagementsystem.entity.Department;
import com.office.employeemanagementsystem.repository.DepartmentRepository;
import com.office.employeemanagementsystem.service.DepartmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/departments")
public class DepartmentController {
  @Autowired
  private DepartmentService departmentService;

  @PostMapping
  public ResponseEntity<Department> save(@Valid @RequestBody Department department) {
    return new ResponseEntity<>(departmentService.saveDepartment(department), HttpStatus.CREATED);
  }

  @GetMapping
  public ResponseEntity<List<Department>> findAll() {
    return new ResponseEntity<>(departmentService.getAllDepartments(), HttpStatus.OK);
  }

  @GetMapping("/id/{departmentId}")
  public ResponseEntity<Department> findById(@PathVariable Long departmentId) {
    return new ResponseEntity<>(departmentService.getDepartmentById(departmentId),HttpStatus.OK);
  }

  @DeleteMapping("/id/{departmentId}")
  public ResponseEntity<?> deleteById(@PathVariable Long departmentId) {
    departmentService.deleteDepartmentById(departmentId);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @PutMapping("/{departmentId}")
  public ResponseEntity<Department> update(@PathVariable Long departmentId, @Valid @RequestBody Department department) {
    return new ResponseEntity<>(departmentService.updateDepartment(departmentId, department), HttpStatus.OK);
  }
}
