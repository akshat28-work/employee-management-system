package com.office.employeemanagementsystem.e2e;

import com.office.employeemanagementsystem.entity.Department;
import com.office.employeemanagementsystem.entity.Employee;
import com.office.employeemanagementsystem.entity.Project;
import com.office.employeemanagementsystem.repository.DepartmentRepository;
import com.office.employeemanagementsystem.repository.EmployeeRepository;
import com.office.employeemanagementsystem.repository.ProjectRepository;
import org.apache.coyote.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalManagementPort;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class EmployeeManagementE2ETest {

  @LocalServerPort
  private int port;

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private PasswordEncoder passwordEncoder;

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

  @BeforeEach
  public void setup() {
    Department department = new Department();
    department.setName("Department 1");
    departmentRepository.save(department);
    Employee employee = new Employee();
    employee.setName("Employee");
    employee.setUsername("admin");
    employee.setPassword(passwordEncoder.encode("admin"));
    employee.setDepartment(department);
    employeeRepository.save(employee);
  }

  private HttpHeaders getAuthenticationHeaders() {
    employeeRepository.deleteAll();
    departmentRepository.deleteAll();

    Department department = new Department();
    department.setName("Auth Dept");
    Department savedDept = departmentRepository.save(department);

    Employee employee = new Employee();
    employee.setName("Admin");
    employee.setUsername("admin");
    employee.setPassword(passwordEncoder.encode("admin"));
    employee.setDepartment(savedDept);
    employeeRepository.save(employee);

    String loginUrl = "http://localhost:" + port + "/auth/login";
    String loginJson = "{\"username\":\"admin\",\"password\":\"admin\"}";

    HttpHeaders loginHeaders = new HttpHeaders();
    loginHeaders.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> entity = new HttpEntity<>(loginJson, loginHeaders);

    ResponseEntity<String> response = restTemplate.postForEntity(loginUrl, entity, String.class);
    String body = response.getBody();
    String token = body.contains("token: ") ? body.substring(body.indexOf("token: ") + 7).trim() : body;

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setBearerAuth(token);
    return headers;
  }

  @Test
  public void testCreationE2E() {
    String url = "http://localhost:" + port;

    HttpHeaders authHeaders = getAuthenticationHeaders();

    Department department = new Department();
    department.setName("Department Name");
    HttpEntity<Department> entity = new HttpEntity<>(department, authHeaders);
    ResponseEntity<Department> response = restTemplate.exchange(url + "/departments", HttpMethod.POST, entity, Department.class);
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    Department savedDepartment = response.getBody();

    String projectJson = "{"
        + "\"projectTitle\":\"Project Title\","
        + "\"department\":{\"id\":" + savedDepartment.getId() + "}"
        + "}";
    HttpEntity<String> request = new HttpEntity<>(projectJson, authHeaders);
    ResponseEntity<Project> projResponse = restTemplate.exchange(url + "/projects", HttpMethod.POST, request, Project.class);
    assertEquals(HttpStatus.CREATED, projResponse.getStatusCode());
    assertEquals("Project Title", projResponse.getBody().getProjectTitle());
    Project savedProject = projResponse.getBody();

    String employeeJson = "{"
        + "\"name\":\"John Doe\","
        + "\"username\":\"johndoe\","
        + "\"password\":\"johndoe\","
        + "\"department\":{\"id\":" + savedDepartment.getId() + "},"
        + "\"projects\":[{\"id\":" + savedProject.getId() + "}]"
        + "}";
    HttpEntity<String> request2 = new HttpEntity<>(employeeJson, authHeaders);
    ResponseEntity<Employee> employeeResponse = restTemplate.exchange(url + "/employees", HttpMethod.POST, request2, Employee.class);
    assertEquals(HttpStatus.CREATED, employeeResponse.getStatusCode());
    assertEquals("John Doe", employeeResponse.getBody().getName());
  }

  @Test
  public void testUpdateEmployeeE2E() {
    String url = "http://localhost:" + port;

    HttpHeaders authHeaders = getAuthenticationHeaders();

    Department department = new Department();
    department.setName("Department Name");
    HttpEntity<Department> entity = new HttpEntity<>(department, authHeaders);
    ResponseEntity<Department> response = restTemplate.exchange(url + "/departments", HttpMethod.POST, entity, Department.class);
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    Department savedDepartment = response.getBody();

    String employeeJson = "{"
        + "\"name\":\"John Doe\","
        + "\"username\":\"johndoe\","
        + "\"password\":\"johndoe\","
        + "\"department\":{\"id\":" + savedDepartment.getId() + "}"
        + "}";
    authHeaders.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> request = new HttpEntity<>(employeeJson, authHeaders);
    ResponseEntity<Employee> employeeResponse = restTemplate.postForEntity(url+"/employees", request, Employee.class);
    Long employeeId = employeeResponse.getBody().getId();

    String updatedJson = "{"
        + "\"name\":\"Updated Name\","
        + "\"username\":\"johndoe\","
        + "\"password\":\"johndoe\","
        + "\"department\":{\"id\":" + savedDepartment.getId() + "}"
        + "}";
    HttpEntity<String> request2 = new HttpEntity<>(updatedJson, authHeaders);
    ResponseEntity<Employee> updatedEmployeeResponse = restTemplate.exchange(url+"/employees/" + employeeId, HttpMethod.PUT, request2, Employee.class);
    assertEquals(HttpStatus.OK, updatedEmployeeResponse.getStatusCode());
    assertEquals("Updated Name", updatedEmployeeResponse.getBody().getName());
  }

  @Test
  public void searchByProjectE2E() {
    String url = "http://localhost:" + port;

    HttpHeaders authHeaders = getAuthenticationHeaders();

    Department department = new Department();
    department.setName("Department Name");
    Department savedDepartment = departmentRepository.save(department);

    Project project = new Project();
    project.setProjectTitle("Project Title");
    project.setDepartment(savedDepartment);
    Project savedProject = projectRepository.save(project);

    String employeeJson = "{"
        + "\"name\":\"John Doe\","
        + "\"username\":\"johndoe\","
        + "\"password\":\"johndoe\","
        + "\"department\":{\"id\":" + savedDepartment.getId() + "},"
        + "\"projects\":[{\"id\":" + savedProject.getId() + "}]"
        + "}";
    HttpEntity<String> postRequest = new HttpEntity<>(employeeJson, authHeaders);
    restTemplate.exchange(url + "/employees", HttpMethod.POST, postRequest, Employee.class);
    HttpEntity<Void> getRequest = new HttpEntity<>(authHeaders);
    ResponseEntity<Employee[]> response = restTemplate.exchange(
        url + "/employees/project/" + savedProject.getId(),
        HttpMethod.GET,
        getRequest,
        Employee[].class);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    Employee[] employees = response.getBody();
    assertNotNull(employees);
    assertTrue(employees.length > 0);
    assertEquals("John Doe", employees[0].getName());
  }

  @Test
  public void deleteProjectIdE2E() {
    String url = "http://localhost:" + port;

    HttpHeaders authHeaders = getAuthenticationHeaders();

    Department department = new Department();
    department.setName("Department Name");
    Department savedDepartment = departmentRepository.save(department);

    Project project = new Project();
    project.setProjectTitle("Project Title");
    project.setDepartment(savedDepartment);
    Project savedProject = projectRepository.save(project);

    HttpEntity<Void> request = new HttpEntity<>(authHeaders);
    ResponseEntity<Void> deleteResponse = restTemplate.exchange(url + "/projects/" + savedProject.getId(),
        HttpMethod.DELETE, request, Void.class);
    assertTrue(deleteResponse.getStatusCode().is2xxSuccessful());
    ResponseEntity<Project[]> response = restTemplate.exchange(
        url + "/projects",
        HttpMethod.GET,
        request,
        Project[].class
    );
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(0, response.getBody().length);
  }
}
