package com.office.employeemanagementsystem.e2e;

import com.office.employeemanagementsystem.entity.Department;
import com.office.employeemanagementsystem.entity.Employee;
import com.office.employeemanagementsystem.entity.Project;
import com.office.employeemanagementsystem.repository.DepartmentRepository;
import com.office.employeemanagementsystem.repository.EmployeeRepository;
import com.office.employeemanagementsystem.repository.ProjectRepository;
import org.apache.coyote.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalManagementPort;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class EmployeeManagementE2ETest {

  @LocalServerPort
  private int port;

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private DepartmentRepository departmentRepository;

  @Autowired
  private ProjectRepository projectRepository;

  @Autowired
  private EmployeeRepository employeeRepository;

  @AfterEach
  public void tearDown(){
    employeeRepository.deleteAll();
    projectRepository.deleteAll();
    departmentRepository.deleteAll();
  }

  @Test
  public void testCreationE2E() {
    String url = "http://localhost:" + port;

    Department department = new Department();
    department.setName("Department Name");
    ResponseEntity<Department> response = restTemplate.postForEntity(url+"/departments", department, Department.class);
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    Department savedDepartment = response.getBody();

    String projectJson = "{"
        + "\"projectTitle\":\"Project Title\","
        + "\"department\":{\"id\":" + savedDepartment.getId() + "}"
        + "}";
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> request = new HttpEntity<>(projectJson, headers);
    ResponseEntity<Project> projResponse = restTemplate.postForEntity(url+"/projects", request, Project.class);
    assertEquals(HttpStatus.CREATED, projResponse.getStatusCode());
    assertEquals("Project Title", projResponse.getBody().getProjectTitle());
    Project savedProject = projResponse.getBody();

    String employeeJson = "{"
        + "\"name\":\"John Doe\","
        + "\"department\":{\"id\":" + savedDepartment.getId() + "},"
        + "\"projects\":[{\"id\":" + savedProject.getId() + "}]"
        + "}";
    HttpHeaders headers2 = new HttpHeaders();
    headers2.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> request2 = new HttpEntity<>(employeeJson, headers2);
    ResponseEntity<Employee>  employeeResponse = restTemplate.postForEntity(url+"/employees", request2, Employee.class);
    assertEquals(HttpStatus.CREATED, employeeResponse.getStatusCode());
    assertEquals("John Doe", employeeResponse.getBody().getName());
  }

  @Test
  public void testUpdateEmployeeE2E() {
    String url = "http://localhost:" + port;
    Department department = new Department();
    department.setName("Department Name");
    ResponseEntity<Department> response = restTemplate.postForEntity(url+"/departments", department, Department.class);
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    Department savedDepartment = response.getBody();

    String employeeJson = "{\"name\":\"Original Name\", \"department\":{\"id\":" + savedDepartment.getId() + "}}";
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> request = new HttpEntity<>(employeeJson, headers);
    ResponseEntity<Employee> employeeResponse = restTemplate.postForEntity(url+"/employees", request, Employee.class);
    Long employeeId = employeeResponse.getBody().getId();

    String updatedJson = "{\"name\":\"Updated Name\", \"department\":{\"id\":" + savedDepartment.getId() + "}}";
    HttpEntity<String> request2 = new HttpEntity<>(updatedJson, headers);
    ResponseEntity<Employee> updatedEmployeeResponse = restTemplate.exchange(url+"/employees/" + employeeId, HttpMethod.PUT, request2, Employee.class);
    assertEquals(HttpStatus.OK, updatedEmployeeResponse.getStatusCode());
    assertEquals("Updated Name", updatedEmployeeResponse.getBody().getName());
  }

  @Test
  public void searchByProjectE2E() {
    String url = "http://localhost:" + port;

    Department department = new Department();
    department.setName("Department Name");
    Department savedDepartment = departmentRepository.save(department);

    Project project = new Project();
    project.setProjectTitle("Project Title");
    project.setDepartment(savedDepartment);
    Project savedProject = projectRepository.save(project);

    String employeeJson = "{"
        + "\"name\":\"Search Tester\","
        + "\"department\":{\"id\":" + savedDepartment.getId() + "},"
        + "\"projects\":[{\"id\":" + savedProject.getId() + "}]"
        + "}";
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> request = new HttpEntity<>(employeeJson, headers);
    restTemplate.postForEntity(url+"/employees", request, Employee.class);
    ResponseEntity<Employee[]> response = restTemplate.getForEntity(url+"/employees/project/"+savedProject.getId(), Employee[].class);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    Employee[] employees = response.getBody();
    assertNotNull(employees);
    assertTrue(employees.length > 0);
    assertEquals("Search Tester", employees[0].getName());
  }

  @Test
  public void deleteProjectIdE2E() {
    String url = "http://localhost:" + port;

    Department department = new Department();
    department.setName("Department Name");
    Department savedDepartment = departmentRepository.save(department);

    Project project = new Project();
    project.setProjectTitle("Project Title");
    project.setDepartment(savedDepartment);
    Project savedProject = projectRepository.save(project);

    restTemplate.delete(url+"/projects/"+savedProject.getId());
    ResponseEntity<Project[]> response = restTemplate.getForEntity(url+"/projects", Project[].class);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(0, response.getBody().length);
  }
}
