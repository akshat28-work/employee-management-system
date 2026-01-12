package com.office.employeemanagementsystem.service;

import com.office.employeemanagementsystem.entity.Department;
import com.office.employeemanagementsystem.entity.Employee;
import com.office.employeemanagementsystem.entity.Project;
import com.office.employeemanagementsystem.repository.DepartmentRepository;
import com.office.employeemanagementsystem.repository.EmployeeRepository;
import com.office.employeemanagementsystem.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {
  @Mock
  private EmployeeRepository employeeRepository;

  @Mock
  private ProjectRepository projectRepository;

  @Mock
  private DepartmentRepository departmentRepository;

  @InjectMocks
  private EmployeeService employeeService;

  @InjectMocks
  private ProjectService projectService;

  @InjectMocks
  private DepartmentService departmentService;

  @Test
  public void findAllEmployeesSuccess() {
    Long departmentId = 1L;
    Department department = new Department();
    department.setId(departmentId);
    department.setName("Department Name");
    Long employeeId = 2L;
    Employee employee = new Employee();
    employee.setId(employeeId);
    employee.setDepartment(department);
    when(employeeRepository.findAll()).thenReturn(List.of(employee));
    List<Employee> employees = employeeService.findAllEmployees();
    assertNotNull(employees);
    assertEquals(1, employees.size());
    verify(employeeRepository, times(1)).findAll();
  }

  @Test
  public void findAllEmployeesFailure() {
    when(employeeRepository.findAll()).thenReturn(List.of());
    List<Employee> employees = employeeService.findAllEmployees();
    assertNotNull(employees);
    assertEquals(0, employees.size());
    verify(employeeRepository, times(1)).findAll();
  }

  @Test
  public void findEmployeeByIdSuccess() {
    Long departmentId = 1L;
    Department department = new Department();
    department.setId(departmentId);
    department.setName("Department Name");
    Long employeeId = 2L;
    Employee employee = new Employee();
    employee.setId(employeeId);
    employee.setDepartment(department);
    when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
    Employee employee1 = employeeService.findEmployeeById(employeeId);
    assertNotNull(employee1);
    assertEquals(employee, employee1);
    verify(employeeRepository, times(1)).findById(employeeId);
  }

  @Test
  public void findEmployeeByIdFailure() {
    Long employeeId = 1L;
    when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());
    RuntimeException exception = assertThrows(RuntimeException.class,
        () -> employeeService.findEmployeeById(employeeId));
    assertEquals("Employee not found with ID: " + employeeId, exception.getMessage());
    verify(employeeRepository, times(1)).findById(employeeId);
  }

  @Test
  public void deleteEmployeeByIdSuccess() {
    Long departmentId = 1L;
    Department department = new Department();
    department.setId(departmentId);
    department.setName("Department Name");
    Long employeeId = 2L;
    Employee employee = new Employee();
    employee.setId(employeeId);
    employee.setDepartment(department);
    employee.setProjects(new ArrayList<>());
    when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
    employeeService.deleteEmployeeById(employeeId);
    verify(employeeRepository, times(1)).deleteById(employeeId);
  }

  @Test
  public void deleteEmployeeByIdFailure() {
    Long employeeId = 1L;
    when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());
    RuntimeException exception = assertThrows(RuntimeException.class,
        () -> employeeService.deleteEmployeeById(employeeId));
    assertEquals("Employee not found with ID: " + employeeId, exception.getMessage());
    verify(employeeRepository, times(1)).findById(employeeId);
    verify(employeeRepository, never()).deleteById(employeeId);
  }

  @Test
  public void getEmployeesByDepartmentIdSuccess() {
    Long departmentId = 1L;
    Department department = new Department();
    department.setId(departmentId);
    department.setName("Department Name");
    Long employeeId = 2L;
    Employee employee = new Employee();
    employee.setId(employeeId);
    employee.setName("Employee Name");
    employee.setDepartment(department);
    employee.setProjects(new ArrayList<>());
    List<Employee> employees = List.of(employee);
    when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(department));
    when(employeeRepository.findByDepartmentId(departmentId)).thenReturn(employees);
    List<Employee> result = employeeService.getEmployeesByDept(departmentId);
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("Employee Name", result.get(0).getName());
    verify(departmentRepository, times(1)).findById(departmentId);
    verify(employeeRepository, times(1)).findByDepartmentId(departmentId);
  }

  @Test
  public void getEmployeesByDepartmentIdFailure() {
    Long departmentId = 1L;
    when(departmentRepository.findById(departmentId)).thenReturn(Optional.empty());
    RuntimeException exception = assertThrows(RuntimeException.class,
        () -> employeeService.getEmployeesByDept(departmentId));
    assertEquals("Department not found", exception.getMessage());
    verify(departmentRepository, times(1)).findById(departmentId);
  }

  @Test
  public void getEmployeesByProjectIdSuccess() {
    Long departmentId = 1L;
    Department department = new Department();
    department.setId(departmentId);
    department.setName("Department Name");
    Long projectId = 1L;
    Project project = new Project();
    project.setId(projectId);
    project.setProjectTitle("Project Title");
    Long employeeId = 2L;
    Employee employee = new Employee();
    employee.setId(employeeId);
    employee.setName("Employee Name");
    employee.setDepartment(department);
    employee.setProjects(new ArrayList<>());
    List<Employee> employees = List.of(employee);
    when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
    when(employeeRepository.findByProjectsId(projectId)).thenReturn(employees);
    List<Employee> result = employeeService.getEmployeesByProject(projectId);
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("Employee Name", result.get(0).getName());
    verify(projectRepository, times(1)).findById(projectId);
  }

  @Test
  public void getEmployeesByProjectIdFailure() {
    Long projectId = 1L;
    when(projectRepository.findById(projectId)).thenReturn(Optional.empty());
    RuntimeException exception = assertThrows(RuntimeException.class,
        () -> employeeService.getEmployeesByProject(projectId));
    assertEquals("Project not found", exception.getMessage());
    verify(projectRepository, times(1)).findById(projectId);
  }

  @Test
  public void saveEmployeeSuccess() {
    Long departmentId = 2L;
    Department department = new Department();
    department.setId(departmentId);
    department.setName("Department Name");
    Long projectId = 1L;
    Project project = new Project();
    project.setId(projectId);
    project.setProjectTitle("Project Title");
    project.setDepartment(department);
    Employee employee = new Employee();
    employee.setName("Employee Name");
    employee.setDepartment(department);
    employee.setProjects(List.of(project));
    when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(department));
    List<Long> pIds = List.of(projectId);
    when(projectRepository.findAllByIdWithDepartment(pIds)).thenReturn(List.of(project));
    when(employeeRepository.save(any(Employee.class))).thenReturn(employee);
    Employee result = employeeService.saveEmployee(employee);
    assertNotNull(result);
    assertEquals("Employee Name", result.getName());
    verify(departmentRepository).findById(departmentId);
    verify(projectRepository).findAllByIdWithDepartment(pIds);
  }

  @Test
  public void saveEmployeeFailureDepartmentMismatch() {
    Department empDept = new Department();
    empDept.setId(2L);
    Department projDept = new Department();
    projDept.setId(5L);
    Project project = new Project();
    project.setId(1L);
    project.setDepartment(projDept);
    Employee employee = new Employee();
    employee.setName("Employee Name");
    employee.setDepartment(empDept);
    employee.setProjects(List.of(project));
    when(departmentRepository.findById(2L)).thenReturn(Optional.of(empDept));
    List<Long> expectedPids = List.of(1L);
    when(projectRepository.findAllByIdWithDepartment(anyList())).thenReturn(List.of(project));    RuntimeException ex = assertThrows(RuntimeException.class, () -> {
      employeeService.saveEmployee(employee);
    });
    System.out.println("ACTUAL ERROR MESSAGE: " + ex.getMessage());
    assertTrue(ex.getMessage().contains("Department Mismatch!"));
    verify(employeeRepository, never()).save(any(Employee.class));
  }

  @Test
  public void saveEmployeeFailureNameBlank() {
    Long departmentId = 1L;
    Department department = new Department();
    department.setId(departmentId);
    Employee employee = new Employee();
    employee.setName("");
    employee.setDepartment(department);
    employee.setProjects(List.of());
    RuntimeException ex = assertThrows(RuntimeException.class,
        () -> {employeeService.saveEmployee(employee);});
    assertTrue(ex.getMessage().contains("Name cannot be blank"));
  }

  @Test
  public void updateEmployeeSuccess() {
    String username = "Employee Name";
    Authentication authentication = mock(Authentication.class);
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getName()).thenReturn(username);
    SecurityContextHolder.setContext(securityContext);
    Long employeeId = 3L;
    Long deptId = 2L;
    Long projId = 1L;
    Department department = new Department();
    department.setId(deptId);
    Project project = new Project();
    project.setId(projId);
    project.setDepartment(department);
    Employee existingEmployee = new Employee();
    existingEmployee.setId(employeeId);
    existingEmployee.setName("Old Name");
    existingEmployee.setUsername(username);
    existingEmployee.setProjects(new ArrayList<>());
    Employee updatedInfo = new Employee();
    updatedInfo.setName("New Name");
    updatedInfo.setDepartment(department);
    updatedInfo.setProjects(List.of(project));
    when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(existingEmployee));
    when(departmentRepository.findById(deptId)).thenReturn(Optional.of(department));
    when(projectRepository.findAllByIdWithDepartment(List.of(projId))).thenReturn(List.of(project));
    when(employeeRepository.save(any(Employee.class))).thenReturn(existingEmployee);
    Employee result = employeeService.updateEmployee(employeeId, updatedInfo);
    assertEquals("New Name", result.getName());
    assertEquals(1, result.getProjects().size());
    verify(employeeRepository).save(existingEmployee);
  }

  @Test
  public void updateEmployeeFailure_NotFound() {
    Authentication authentication = mock(Authentication.class);
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getName()).thenReturn("admin");
    SecurityContextHolder.setContext(securityContext);
    Long employeeId = 99L;
    when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());
    assertThrows(RuntimeException.class, () -> {
      employeeService.updateEmployee(employeeId, new Employee());
    });
    verify(employeeRepository, never()).save(any());
  }
}
