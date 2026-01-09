package com.office.employeemanagementsystem.service;

import com.office.employeemanagementsystem.entity.Department;
import com.office.employeemanagementsystem.repository.DepartmentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DepartmentServiceTest {

  @Mock
  private DepartmentRepository departmentRepository;

  @InjectMocks
  private DepartmentService departmentService;

  @Test
  public void saveDepartmentTestSuccess() {
    Department department = new Department();
    department.setName("DEPT_NAME");
    when(departmentRepository.save(department)).thenReturn(department);
    Department savedDepartment = departmentService.saveDepartment(department);
    assertNotNull(savedDepartment);
    assertEquals(department.getName(), savedDepartment.getName());
    verify(departmentRepository, times(1)).save(department);
  }

  @Test
  public void departmentNotFoundById(){
    Long departmentId = 1L;
    when(departmentRepository.findById(departmentId)).thenReturn(Optional.empty());
    RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> {
      departmentService.getDepartmentById(departmentId);
    });
    assertEquals("Department with ID " + departmentId + " not found", runtimeException.getMessage());
    verify(departmentRepository, times(1)).findById(departmentId);
  }

  @Test
  public void departmentFoundById(){
    Long departmentId = 1L;
    Department department = new Department();
    department.setName("DEPT_NAME");
    when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(department));
    Department savedDepartment = departmentService.getDepartmentById(departmentId);
    assertNotNull(savedDepartment);
    assertEquals(department.getName(), savedDepartment.getName());
  }

  @Test
  public void getAllDepartmentsTestSuccess(){
    Department department = new Department();
    department.setName("DEPT_NAME");
    List<Department> departments = new ArrayList<>();
    departments.add(department);
    when(departmentRepository.findAll()).thenReturn(departments);
    List<Department> savedDepartments = departmentService.getAllDepartments();
    assertNotNull(savedDepartments);
    assertEquals(departments.size(), savedDepartments.size());
  }

  @Test
  public void deleteDepartmentFailure(){
    Long departmentId = 1L;
    when(departmentRepository.findById(departmentId)).thenReturn(Optional.empty());
    EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
      departmentService.deleteDepartmentById(departmentId);
    });
    assertEquals(("Department with ID " + departmentId + " not found"), exception.getMessage());
    verify(departmentRepository, never()).deleteById(departmentId);
  }

  @Test
  public void deleteDepartmentSuccess(){
    Long departmentId = 1L;
    Department department = new Department();
    department.setName("DEPT_NAME");
    when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(department));
    departmentService.deleteDepartmentById(departmentId);
    verify(departmentRepository, times(1)).deleteById(departmentId);
  }

  @Test
  public void updateDepartmentFailure(){
    Long departmentId = 1L;
    Department department = new Department();
    department.setName("DEPT_NAME");
    when(departmentRepository.findById(departmentId)).thenReturn(Optional.empty());
    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
      departmentService.updateDepartment(departmentId, department);
    });
    assertEquals(("Department with ID " + departmentId + " not found"), exception.getMessage());
    verify(departmentRepository, times(1)).findById(departmentId);
    verify(departmentRepository, never()).save(any(Department.class));
  }

  @Test
  public void updateDepartmentSuccess(){
    Long departmentId = 1L;
    Department oldDepartment = new Department();
    oldDepartment.setName("DEPT_NAME");
    Department newDepartment = new Department();
    newDepartment.setName("NEW_DEPARTMENT");
    when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(oldDepartment));
    when(departmentRepository.save(newDepartment)).thenReturn(newDepartment);
    Department savedDepartment = departmentService.updateDepartment(departmentId, newDepartment);
    assertNotNull(savedDepartment);
    verify(departmentRepository, times(1)).findById(departmentId);
    verify(departmentRepository, times(1)).save(newDepartment);
  }
}
