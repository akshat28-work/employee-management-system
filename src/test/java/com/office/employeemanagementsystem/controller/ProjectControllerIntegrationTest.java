package com.office.employeemanagementsystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.office.employeemanagementsystem.entity.Department;
import com.office.employeemanagementsystem.entity.Project;
import com.office.employeemanagementsystem.repository.DepartmentRepository;
import com.office.employeemanagementsystem.repository.EmployeeRepository;
import com.office.employeemanagementsystem.repository.ProjectRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class ProjectControllerIntegrationTest {

  @Autowired
  private EntityManager entityManager;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private DepartmentRepository departmentRepository;

  @Autowired
  private ProjectRepository projectRepository;

  @BeforeEach
  public void setup() {
    departmentRepository.deleteAll();
    projectRepository.deleteAll();
  }

  @Test
  public void createProjectTest() throws Exception {
    Department department = new Department();
    department.setName("Department 1");
    Department savedDepartment = departmentRepository.save(department);
    String projectJson = "{"
        + "\"projectTitle\":\"Project 1\","
        + "\"projectDescription\":\"Description 1\","
        + "\"department\":{\"id\":" + savedDepartment.getId() + "}"
        + "}";
    mockMvc.perform(post("/projects")
        .contentType(MediaType.APPLICATION_JSON)
        .content(projectJson))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.projectTitle").value("Project 1"));
    Project databaseProject = projectRepository.findAll().stream()
        .filter(p -> p.getProjectTitle().equals("Project 1"))
        .findFirst()
        .orElseThrow();
    assertNotNull(databaseProject.getDepartment(), "Department not found");
    assertEquals(savedDepartment.getId(), databaseProject.getDepartment().getId());
  }

  @Test
  public void getProjectByDepartmentIdTest() throws Exception {
    Department department = new Department();
    department.setName("Department 1");
    Department savedDepartment = departmentRepository.save(department);
    Project project = new Project();
    project.setProjectTitle("Project 1");
    project.setDepartment(savedDepartment);
    Project savedProj = projectRepository.save(project);
    mockMvc.perform(get("/projects/department/"+savedDepartment.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].projectTitle").value("Project 1"))
        .andExpect(jsonPath("$[0].id").exists());
  }

  @Test
  public void updateProjectTest() throws Exception {
    Department department = new Department();
    department.setName("Department 1");
    Department savedDepartment = departmentRepository.save(department);
    Project oldProject = new Project();
    oldProject.setProjectTitle("Project 1");
    oldProject.setProjectDescription("Old Project");
    oldProject.setDepartment(savedDepartment);
    Project savedOldProj = projectRepository.save(oldProject);
    String updatedProjectJson = "{"
        + "\"projectTitle\":\"New Updated Title\","
        + "\"projectDescription\":\"New Updated Description\","
        + "\"department\":{\"id\":" + savedDepartment.getId() + "}"
        + "}";
    mockMvc.perform(put("/projects/"+savedOldProj.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .content(updatedProjectJson))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.projectTitle").value("New Updated Title"))
        .andExpect(jsonPath("$.projectDescription").value("New Updated Description"));
    Project databaseProject = projectRepository.findById(oldProject.getId()).orElseThrow();
    assertEquals("New Updated Title", databaseProject.getProjectTitle());
    assertEquals("New Updated Description", databaseProject.getProjectDescription());
  }

  @Test
  public void deleteProjectTest() throws Exception {
    Department department = new Department();
    department.setName("Department 1");
    Department savedDepartment = departmentRepository.save(department);
    Project project = new Project();
    project.setProjectTitle("Project 1");
    project.setDepartment(savedDepartment);
    Project savedProj = projectRepository.save(project);
    mockMvc.perform(delete("/projects/"+project.getId()))
        .andExpect(status().isNoContent());
  }
}
