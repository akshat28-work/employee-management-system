package com.office.employeemanagementsystem.controller;

import com.office.employeemanagementsystem.entity.Department;
import com.office.employeemanagementsystem.entity.Project;
import com.office.employeemanagementsystem.service.DepartmentService;
import com.office.employeemanagementsystem.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects")
public class ProjectController {
  @Autowired
  private ProjectService projectService;

  @Autowired
  private DepartmentService departmentService;

  @PostMapping
  public ResponseEntity<Project> save(@Valid @RequestBody Project project){
    return new ResponseEntity<>(projectService.saveProject(project), HttpStatus.CREATED);
  }

  @GetMapping("/department/{departmentId}")
  public ResponseEntity<List<Project>> getAllProjectsByDepartment(@PathVariable Long departmentId){
    return new ResponseEntity<>(projectService.getProjectsByDepartment(departmentId), HttpStatus.OK);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Project> updateProject(@PathVariable Long id, @Valid @RequestBody Project project){
    return new ResponseEntity<>(projectService.updateProject(id, project), HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<String> deleteProject(@PathVariable Long id){
    projectService.deleteProjectById(id);
    return new ResponseEntity<>("Project deleted successfully", HttpStatus.NO_CONTENT);
  }

  @GetMapping
  public ResponseEntity<List<Project>> getAllProjects(){
    return new ResponseEntity<>(projectService.getAllProjects(), HttpStatus.OK);
  }
}
