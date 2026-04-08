package com.example.hogwarts.demo.controller;

import com.example.hogwarts.demo.model.Student;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class StudentControllerRestTemplateTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void contextLoads() {
        assertThat(restTemplate).isNotNull();
    }

    @Test
    void testDefaultMessage() {
        String result = restTemplate.getForObject(
                "http://localhost:" + port + "/",
                String.class
        );
        assertThat(result).isNotNull();
    }

    @Test
    void testGetStudents() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/students",
                String.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void testPostStudents() {
        Student student = new Student();
        student.setName("Daniel");
        student.setAge(17);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Student> request = new HttpEntity<>(student, headers);

        ResponseEntity<Student> response = restTemplate.exchange(
                "http://localhost:" + port + "/students",
                HttpMethod.POST,
                request,
                Student.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Daniel");
        assertThat(response.getBody().getAge()).isEqualTo(17);
    }
}