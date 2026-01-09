package com.office.employeemanagementsystem;

import com.office.employeemanagementsystem.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@SpringBootTest
@ActiveProfiles("test")
public class SmokeTest {

  @Autowired
  private EmployeeService employeeService;

  @Test
  void contextLoads() {
    assertThat(employeeService).isNotNull();
  }
}
