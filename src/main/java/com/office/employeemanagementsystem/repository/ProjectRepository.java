package com.office.employeemanagementsystem.repository;

import com.office.employeemanagementsystem.entity.Department;
import com.office.employeemanagementsystem.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
  List<Project> findByDepartment(Department department);
}
