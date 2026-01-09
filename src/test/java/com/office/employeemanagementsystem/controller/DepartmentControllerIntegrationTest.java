package com.office.employeemanagementsystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.office.employeemanagementsystem.entity.Department;
import com.office.employeemanagementsystem.repository.DepartmentRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class DepartmentControllerIntegrationTest {
  @Autowired
  private DepartmentRepository departmentRepository;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @BeforeEach
  public void setup() {
    departmentRepository.deleteAll();
  }

  @Test
  public void createDepartmentTest() throws Exception {
    Department department = new Department();
    department.setName("Department Name");
    mockMvc.perform(post("/departments")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(department)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("Department Name"))
        .andExpect(jsonPath("$.id").exists());
  }

  @Test
  public void getAllDepartmentsTest() throws Exception {
    Department department = new Department();
    department.setName("Department Name");
    departmentRepository.save(department);
    mockMvc.perform(get("/departments")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(department)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].name").value("Department Name"))
        .andExpect(jsonPath("$[0].id").exists());
  }

  @Test
  public void updateDepartmentTest() throws Exception {
    Department department = new Department();
    department.setName("Old Name");
    department = departmentRepository.save(department);
    Department newDepartment = new Department();
    newDepartment.setName("New Name");
    newDepartment = departmentRepository.save(newDepartment);
    mockMvc.perform(put("/departments/"+department.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(newDepartment)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("New Name"))
        .andExpect(jsonPath("$.id").exists());
  }

  @Test
  public void deleteDepartmentTest() throws Exception {
    Department department = new Department();
    department.setName("Department Name");
    department = departmentRepository.save(department);
    mockMvc.perform(delete("/departments/id/"+department.getId()))
        .andExpect(status().isNoContent());
  }
}