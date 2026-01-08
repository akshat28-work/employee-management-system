package com.office.employeemanagementsystem.repository;

import com.office.employeemanagementsystem.entity.Department;
import com.office.employeemanagementsystem.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
  List<Project> findByDepartment(Department department);

  // To make sure that an employee from say "x" department doesn't get assigned a project from say "y" department
  @Query("SELECT p FROM Project p JOIN FETCH p.department WHERE p.id IN :ids")
  List<Project> findAllByIdWithDepartment(@Param("ids") List<Long> ids);
}
