package com.office.employeemanagementsystem.service;

import com.office.employeemanagementsystem.entity.Department;
import com.office.employeemanagementsystem.entity.Project;
import com.office.employeemanagementsystem.repository.DepartmentRepository;
import com.office.employeemanagementsystem.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {
  @Mock
  ProjectRepository projectRepository;

  @Mock
  DepartmentRepository departmentRepository;

  @InjectMocks
  ProjectService projectService;

  @Test
  public void saveProjectSuccess() {
    Long departmentId = 1L;
    Department department = new Department();
    department.setId(departmentId);
    department.setName("Department Name");

    Project project = new Project();
    project.setDepartment(department);
    project.setProjectTitle("Project Title");
    project.setProjectDescription("Project Description");

    when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(department));
    when(projectRepository.save(any(Project.class))).thenReturn(project);
    Project savedProject = projectService.saveProject(project);

    assertNotNull(savedProject);
    assertEquals("Project Title", savedProject.getProjectTitle());
    assertEquals("Project Description", savedProject.getProjectDescription());
    verify(projectRepository, times(1)).save(any(Project.class));
  }

  @Test
  public void getAllProjectsSuccess() {
    Long departmentId = 1L;
    Department department = new Department();
    department.setId(departmentId);
    department.setName("Department Name");
    Project project = new Project();
    project.setProjectTitle("Project Title");
    project.setProjectDescription("Project Description");
    project.setDepartment(department);
    when(projectRepository.findAll()).thenReturn(List.of(project));
    List<Project> projects = projectService.getAllProjects();
    assertNotNull(projects);
    assertEquals(1, projects.size());
    verify(projectRepository, times(1)).findAll();
  }

  @Test
  public void getAllProjectsFail() {
    when(projectRepository.findAll()).thenReturn(Collections.emptyList());
    List<Project> projects = projectService.getAllProjects();
    assertNotNull(projects);
    assertEquals(0, projects.size());
    verify(projectRepository, times(1)).findAll();
  }

  @Test
  public void getProjectByDepartmentSuccess() {
    Long departmentId = 1L;
    Department department = new Department();
    department.setId(departmentId);
    department.setName("Department Name");
    Project project = new Project();
    project.setProjectTitle("Project Title");
    project.setProjectDescription("Project Description");
    project.setDepartment(department);
    List<Project> projects = List.of(project);
    when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(department));
    when(projectRepository.findByDepartment(department)).thenReturn(projects);
    List<Project> projectsByDepartment = projectService.getProjectsByDepartment(departmentId);
    assertNotNull(projectsByDepartment);
    assertEquals(1, projectsByDepartment.size());
    verify(projectRepository, times(1)).findByDepartment(department);
  }

  @Test
  public void getProjectByDepartmentFailure() {
    Long departmentId = 1L;
    when(departmentRepository.findById(departmentId)).thenReturn(Optional.empty());
    RuntimeException exception = assertThrows(RuntimeException.class,
        () -> projectService.getProjectsByDepartment(departmentId));
    assertEquals("Department not found with ID: "+departmentId, exception.getMessage());
    verify(departmentRepository, times(1)).findById(departmentId);
  }

  @Test
  public void deleteProjectByIdSuccess() {
    Long projectId = 1L;
    Project project = new Project();
    project.setProjectTitle("Project Title");
    project.setId(projectId);
    when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
    projectService.deleteProjectById(projectId);
    verify(projectRepository, times(1)).findById(projectId);
    verify(projectRepository, times(1)).delete(any(Project.class));
  }

  @Test
  public void deleteProjectByIdFailure() {
    Long projectId = 1L;
    when(projectRepository.findById(projectId)).thenReturn(Optional.empty());
    RuntimeException exception = assertThrows(RuntimeException.class,
        () -> projectService.deleteProjectById(projectId));
    assertEquals("Project not found with ID: " + projectId, exception.getMessage());
    verify(projectRepository, times(1)).findById(projectId);
  }

  @Test
  public void updateProjectByIdSuccess() {
    Long projectId = 1L;
    Long departmentId = 1L;
    Department department = new Department();
    department.setId(departmentId);
    department.setName("Department Name");

    Project oldProject = new Project();
    oldProject.setProjectTitle("Project Title");
    oldProject.setDepartment(department);
    Project newProject = new Project();
    newProject.setProjectTitle("New Project Title");
    newProject.setDepartment(department);

    when(projectRepository.findById(projectId)).thenReturn(Optional.of(oldProject));
    when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(department));
    when(projectRepository.save(any(Project.class))).thenReturn(oldProject);

    projectService.updateProject(projectId, newProject);
    assertNotNull(oldProject);
    assertEquals("New Project Title", oldProject.getProjectTitle());
    verify(departmentRepository, times(1)).findById(departmentId);
    verify(projectRepository, times(1)).findById(projectId);
  }

  @Test
  public void updateProjectByIdFailureProjectDoesNotExist() {
    Long departmentId = 1L;
    Department department = new Department();
    department.setId(departmentId);
    department.setName("Department Name");
    Long projectId = 1L;
    Project project = new Project();
    project.setProjectTitle("Project Title");
    project.setId(projectId);
    project.setDepartment(department);
    when(projectRepository.findById(projectId)).thenReturn(Optional.empty());
    RuntimeException exception = assertThrows(RuntimeException.class,
        () -> projectService.updateProject(projectId, project));
    assertEquals("Project not found with ID: " + projectId, exception.getMessage());
    verify(projectRepository, never()).save(any(Project.class));
  }

  @Test
  public void updateProjectByIdFailureDepartmentDoesNotExist() {
    Long projectId = 1L;
    Long departmentId = 2L;
    Department department = new Department();
    department.setId(departmentId);
    department.setName("Department Name");
    Project project = new Project();
    project.setProjectTitle("Project Title");
    project.setDepartment(department);
    when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
    when(departmentRepository.findById(departmentId)).thenReturn(Optional.empty());
    RuntimeException exception = assertThrows(RuntimeException.class,
        () -> projectService.updateProject(projectId, project));
    assertEquals("Department not found with ID: " + departmentId, exception.getMessage());
    verify(projectRepository, never()).save(any(Project.class));
  }
}
