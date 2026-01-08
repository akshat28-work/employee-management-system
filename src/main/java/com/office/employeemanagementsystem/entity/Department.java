package com.office.employeemanagementsystem.entity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Department {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank(message = "Department name can not be null")
  private String name;

  @OneToMany(mappedBy = "department", cascade = CascadeType.ALL)
  @JsonManagedReference
  private List<Project> projects;

  @OneToMany(mappedBy = "department", cascade = CascadeType.ALL)
  @JsonIgnoreProperties("department")
  private List<Employee> employees;
}
