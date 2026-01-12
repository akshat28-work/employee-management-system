package com.office.employeemanagementsystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.office.employeemanagementsystem.entity.Department;
import com.office.employeemanagementsystem.entity.Employee;
import com.office.employeemanagementsystem.entity.Project;
import com.office.employeemanagementsystem.repository.DepartmentRepository;
import com.office.employeemanagementsystem.repository.EmployeeRepository;
import com.office.employeemanagementsystem.repository.ProjectRepository;
import com.office.employeemanagementsystem.service.DepartmentService;
import com.office.employeemanagementsystem.service.EmployeeService;
import com.office.employeemanagementsystem.service.ProjectService;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Transactional
public class EmployeeControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private EmployeeRepository employeeRepository;

  @Autowired
  private ProjectRepository projectRepository;

  @Autowired
  private DepartmentRepository departmentRepository;

  @BeforeEach
  public void setup() {
    employeeRepository.deleteAll();
    departmentRepository.deleteAll();
    projectRepository.deleteAll();
    Department department = new Department();
    department.setName("Department 1");
    Department savedDepartment = departmentRepository.save(department);
    Employee employee = new Employee();
    employee.setName("Test Admin");
    employee.setDepartment(savedDepartment);
    employee.setUsername("admin");
    employee.setPassword("admin123");
    employeeRepository.save(employee);
  }

  @Test
  @WithMockUser
  public void createEmployeeTest() throws Exception {
    Department department = new Department();
    department.setName("Department 1");
    department = departmentRepository.save(department);
    Project project = new Project();
    project.setProjectTitle("Project 1");
    project.setDepartment(department); // Ensure this link exists!
    project = projectRepository.save(project);
    String employeeJson = "{"
        + "\"name\":\"John Doe\","
        + "\"username\":\"johndoe\","
        + "\"password\":\"johndoe123\","
        + "\"department\":{\"id\":" + department.getId() + "},"
        + "\"projects\":[{\"id\":" + project.getId() + "}]"
        + "}";
    mockMvc.perform(post("/employees")
        .contentType(MediaType.APPLICATION_JSON)
        .content(employeeJson))
        .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("John Doe"));
  }

  @Test
  @WithMockUser
  public void getAllEmployees() throws Exception {
    Department department = new Department();
    department.setName("Department 1");
    Department savedDepartment = departmentRepository.save(department);
    Employee employee = new Employee();
    employee.setName("Employee 1");
    employee.setUsername("employee1");
    employee.setPassword("employee1123");
    employee.setDepartment(savedDepartment);
    Employee savedEmployee = employeeRepository.save(employee);
    mockMvc.perform(get("/employees")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(employee)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.[1].name").value(savedEmployee.getName()));
  }

  @Test
  @WithMockUser(username = "employee1")
  public void updateEmployee() throws Exception {
    Department department = new Department();
    department.setName("Department 1");
    Department savedDepartment = departmentRepository.save(department);
    Project project = new Project();
    project.setProjectTitle("Project 1");
    project.setDepartment(savedDepartment);
    Project savedProject = projectRepository.save(project);
    Employee employee = new Employee();
    employee.setName("Employee 1");
    employee.setUsername("employee1");
    employee.setPassword("employee1123");
    employee.setDepartment(savedDepartment);
    Employee savedEmployee = employeeRepository.save(employee);
    String updateJson = "{"
        + "\"name\":\"Updated Employee\","
        + "\"username\":\"employee1\","
        + "\"password\":\"employee1123\","
        + "\"department\":{\"id\":" + savedDepartment.getId() + "},"
        + "\"projects\":[{\"id\":" + savedProject.getId() + "}]"
        + "}";
    mockMvc.perform(put("/employees/" + savedEmployee.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(updateJson))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Updated Employee"));
  }

  @Test
  @WithMockUser(username = "admin")
  public void deleteEmployee() throws Exception {
    Department department = new Department();
    department.setName("Department 1");
    Department savedDepartment = departmentRepository.save(department);
    Employee employee = new Employee();
    employee.setName("Employee 1");
    employee.setUsername("employee1");
    employee.setPassword("employee1123");
    employee.setDepartment(savedDepartment);
    Employee savedEmployee = employeeRepository.save(employee);
    mockMvc.perform(delete("/employees/"+savedEmployee.getId()))
        .andExpect(status().isOk());
  }
}
