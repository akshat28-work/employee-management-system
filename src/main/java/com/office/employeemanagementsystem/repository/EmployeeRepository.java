package com.office.employeemanagementsystem.repository;

import com.office.employeemanagementsystem.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
  List<Employee> findByDepartmentId(Long departmentId);
  List<Employee> findByProjectsId(Long projectId);
  Optional<Employee> findByUsername(String username);
}
