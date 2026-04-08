package com.example.hogwarts.demo.controller;

import com.example.hogwarts.demo.model.Faculty;
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
public class FacultyControllerRestTemplateTest {

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
    void testGetFacultys() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/facultys",
                String.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void testPostFaculty() {
        Faculty faculty = new Faculty();
        faculty.setName("Hogwarts");
        faculty.setColor("Green");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Faculty> request = new HttpEntity<>(faculty, headers);

        ResponseEntity<Faculty> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/facultys",
                request,
                Faculty.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Hogwarts");
    }

    @Test
    void testGetFacultyById() {
        // сначала создаём
        Faculty faculty = new Faculty();
        faculty.setName("Gryffindor");
        faculty.setColor("Red");

        Faculty created = restTemplate.postForObject(
                "http://localhost:" + port + "/facultys",
                faculty,
                Faculty.class
        );
        Long id = created.getId();

        ResponseEntity<Faculty> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/facultys/" + id,
                Faculty.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Gryffindor");
    }

    @Test
    void testUpdateFaculty() {
        // создаём
        Faculty faculty = new Faculty();
        faculty.setName("Old");
        faculty.setColor("Black");
        Faculty created = restTemplate.postForObject(
                "http://localhost:" + port + "/facultys",
                faculty,
                Faculty.class
        );

        // обновляем
        created.setName("Slytherin");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Faculty> request = new HttpEntity<>(created, headers);

        ResponseEntity<Faculty> response = restTemplate.exchange(
                "http://localhost:" + port + "/facultys",
                HttpMethod.PUT,
                request,
                Faculty.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Slytherin");
    }
}
