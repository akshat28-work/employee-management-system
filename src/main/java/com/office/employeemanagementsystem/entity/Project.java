package com.office.employeemanagementsystem.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.apache.catalina.User;

import java.util.List;

@Entity
@Data
public class Project {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank(message = "Project title can not be empty")
  private String projectTitle;

  private String projectDescription;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "department_id", nullable = false)
  @JsonBackReference
  private Department department;

  @ManyToMany(mappedBy = "projects")
  @JsonIgnoreProperties("properties")
  private List<Employee> employees;
}
