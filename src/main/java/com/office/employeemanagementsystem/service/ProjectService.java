package com.office.employeemanagementsystem.service;

import com.office.employeemanagementsystem.entity.Department;
import com.office.employeemanagementsystem.entity.Project;
import com.office.employeemanagementsystem.repository.DepartmentRepository;
import com.office.employeemanagementsystem.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {
  @Autowired
  private DepartmentRepository departmentRepository;
  @Autowired
  private ProjectRepository projectRepository;

  public Project saveProject(Project project) {
    Long departmentId = project.getDepartment().getId();
    Department department = departmentRepository.findById(departmentId)
        .orElseThrow(() -> new RuntimeException("Department not found with ID: "+departmentId));
    project.setDepartment(department);
    return projectRepository.save(project);
  }

  public List<Project> getProjectsByDepartment(Long departmentId) {
    if(!departmentRepository.findById(departmentId).isPresent()){
      throw new RuntimeException("Department not found with ID: "+departmentId);
    }
    return projectRepository.findByDepartment(departmentRepository.findById(departmentId).get());
  }

  public Project updateProject(Long id, Project projectDetails) {
    Project existingProject = projectRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Project not found with ID: "+id));
    Long departmentId = projectDetails.getDepartment().getId();
    Department department = departmentRepository.findById(departmentId)
        .orElseThrow(() -> new RuntimeException("Department not found with ID: "+departmentId));
    existingProject.setDepartment(department);
    existingProject.setProjectTitle(projectDetails.getProjectTitle());
    existingProject.setProjectDescription(projectDetails.getProjectDescription());
    return projectRepository.save(existingProject);
  }

  public void deleteProjectById(Long id) {
    Project project =  projectRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Project not found with ID: "+id));
    projectRepository.delete(project);
  }

  public List<Project> getAllProjects(){
    return projectRepository.findAll();
  }
}
