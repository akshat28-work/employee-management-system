package com.office.employeemanagementsystem.service;

import com.office.employeemanagementsystem.entity.Department;
import com.office.employeemanagementsystem.entity.Employee;
import com.office.employeemanagementsystem.entity.Project;
import com.office.employeemanagementsystem.repository.DepartmentRepository;
import com.office.employeemanagementsystem.repository.EmployeeRepository;
import com.office.employeemanagementsystem.repository.ProjectRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {
  @Autowired
  private EmployeeRepository employeeRepository;

  @Autowired
  private DepartmentRepository departmentRepository;

  @Autowired
  private ProjectRepository projectRepository;

  public List<Employee> getEmployeesByDept(Long deptId) {
    if(!departmentRepository.findById(deptId).isPresent()) {
      throw new RuntimeException("Department not found");
    }
    return employeeRepository.findByDepartmentId(deptId);
  }

  public List<Employee> getEmployeesByProject(Long projId) {
    if(!projectRepository.findById(projId).isPresent()) {
      throw new RuntimeException("Project not found");
    }
    return employeeRepository.findByProjectsId(projId);
  }

  @Transactional
  public Employee saveEmployee(Employee employee) {
    Long empDeptId = employee.getDepartment().getId();
    Department department = departmentRepository.findById(empDeptId)
        .orElseThrow(() -> new RuntimeException("Department not found"));
    List<Long> pIds = employee.getProjects().stream()
        .map(Project::getId).toList();
    List<Project> dbProjects = projectRepository.findAllByIdWithDepartment(pIds);
    if (dbProjects.size() != pIds.size()) {
      throw new RuntimeException("Validation Error: One or more Project IDs are invalid.");
    }
    for (Project project : dbProjects) {
      if (project.getDepartment() == null) {
        throw new RuntimeException("Project " + project.getId() + " is missing a Dept in DB!");
      }
      long projDeptIdVal = project.getDepartment().getId().longValue();
      long empDeptIdVal = empDeptId.longValue();
      System.out.println("COMPARING: Project Dept (" + projDeptIdVal + ") vs Employee Dept (" + empDeptIdVal + ")");
      if (projDeptIdVal != empDeptIdVal) {
        throw new RuntimeException("Department Mismatch! Employee is in Dept " + empDeptIdVal +
            " but Project " + project.getId() + " belongs to Dept " + projDeptIdVal);
      }
    }
    employee.setDepartment(department);
    employee.setProjects(dbProjects);
    return employeeRepository.save(employee);
  }

  @Transactional
  public Employee updateEmployee(Long id, Employee employee) {
    Employee existingEmployee = employeeRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Employee not found!"));
    Long deptId = employee.getDepartment().getId();
    Department department = departmentRepository.findById(deptId)
        .orElseThrow(() -> new RuntimeException("Department not found!"));
    List<Long> Ids = employee.getProjects().stream()
        .map(Project::getId)
        .toList();
    List<Project> dbProjects = projectRepository.findAllByIdWithDepartment(Ids);
    for (Project project : dbProjects) {
      if (project.getDepartment().getId().longValue() != deptId.longValue() ) {
        throw new RuntimeException("Project and department do not match!");
      }
    }
    existingEmployee.setName(employee.getName());
    existingEmployee.setDepartment(department);
    existingEmployee.getProjects().clear();
    existingEmployee.getProjects().addAll(dbProjects);
    return employeeRepository.save(existingEmployee);
  }

  public Employee findEmployeeById(Long id) {
    return employeeRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + id));
  }

  public List<Employee> findAllEmployees() {
    return employeeRepository.findAll();
  }

  public void deleteEmployeeById(Long id) {
    employeeRepository.deleteById(id);
  }
}