package com.office.employeemanagementsystem.service;

import com.office.employeemanagementsystem.entity.Department;
import com.office.employeemanagementsystem.repository.DepartmentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentService {
  @Autowired
  private DepartmentRepository departmentRepository;

  public Department saveDepartment(Department department) {
    return departmentRepository.save(department);
  }

  public List<Department> getAllDepartments(){
    return departmentRepository.findAll();
  }

  public Department getDepartmentById(Long id) {
    return departmentRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Department with ID " + id + " not found"));
  }

  public void deleteDepartmentById(Long id){
    if(!departmentRepository.findById(id).isPresent()){
      throw new EntityNotFoundException("Department with ID " + id + " not found");
    }
    departmentRepository.deleteById(id);
  }

  public Department updateDepartment(Long id, Department department) {
    Department existingDepartment = departmentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Department with ID " + id + " not found"));
    existingDepartment.setName(department.getName());
    return departmentRepository.save(existingDepartment);
  }
}
