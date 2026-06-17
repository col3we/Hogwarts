package com.example.hogwarts.demo.controller;

import com.example.hogwarts.demo.model.Avatar;
import com.example.hogwarts.demo.model.Student;
import com.example.hogwarts.demo.service.AvatarService;
import com.example.hogwarts.demo.service.StudentService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/students")
public class StudentController {
    private final StudentService studentService;
    private final AvatarService avatarService;

    public StudentController(StudentService studentService, AvatarService avatarService) {
        this.studentService = studentService;
        this.avatarService = avatarService;
    }

    @GetMapping("/{id}") // GET http://localhost:8080/students/
    public ResponseEntity<Student> getStudentInfo(@PathVariable Long id) {
        Student student = studentService.findStudent(id);
        if (student == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(student);
    }

    @GetMapping  // GET http://localhost:8080/students
    public ResponseEntity<Collection<Student>> getAllStudents() {
        return ResponseEntity.ok(studentService.getAllStudents());
    }

    @PostMapping()  // POST http://localhost:8080/students
    public Student createStudent(@RequestBody Student student) {
        return studentService.createStudent(student);
    }

    @PutMapping  // PUT http://localhost:8080/students
    public ResponseEntity<Student> editStudent(@RequestBody Student student) {
        Student foundStudent = studentService.editStudent(student);
        if (foundStudent == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(foundStudent);
    }

    @DeleteMapping("/{id}")  // DELETE http://localhost:8080/students/
    public ResponseEntity deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/age/{age}")      // GET http://localhost:8080/students/age/
    public Collection<Student> getStudentsByAge(@PathVariable int age) {
        return studentService.findByAge(age);
    }

    @GetMapping("/age/{age1}/{age2}")   // GET http://localhost:8080/students/age/
    public Collection<Student> getStudentsByAgeBetween(@PathVariable int age1, @PathVariable int age2) {
        return studentService.findByAgeBetween(age1, age2);
    }

    @PostMapping(value = "/{id}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)    // Post http://localhost:8080/id/avatar/
    public ResponseEntity<String> uploadAvatar(@PathVariable Long id, @RequestParam MultipartFile avatar) throws IOException {
        if (avatar.isEmpty() || avatar.getSize() >= 1024 * 300) {
            return ResponseEntity.badRequest().body("File is empty or too big (max 300KB)");
        }

        avatarService.uploadAvatar(id, avatar);
        return ResponseEntity.ok("Avatar uploaded successfully");
    }

    @GetMapping(value = "/{id}/avatar/preview")      // Get http://localhost:8080/id/avatar/preview
    public ResponseEntity<byte[]> downloadAvatarPreview(@PathVariable Long id) {
        Avatar avatar = avatarService.findByStudentId(id);
        if (avatar == null || avatar.getData() == null) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(avatar.getMediaType()));
        headers.setContentLength(avatar.getData().length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(avatar.getData());
    }

    @GetMapping(value = "/{id}/avatar")      // Get http://localhost:8080/id/avatar/
    public void downloadAvatar(@PathVariable Long id, HttpServletResponse response) throws IOException {
        Avatar avatar = avatarService.findByStudentId(id);
        if (avatar == null || avatar.getFilePath() == null) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return;
        }

        Path path = Path.of(avatar.getFilePath());
        if (!Files.exists(path)) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return;
        }

        try (InputStream is = Files.newInputStream(path);
             OutputStream os = response.getOutputStream()) {
            response.setContentType(avatar.getMediaType());
            response.setContentLengthLong(avatar.getFileSize());
            is.transferTo(os);
        }
    }

    @GetMapping("/count")    // Get http://localhost:8080/students/count/
    public long getStudentsCount() {
        return studentService.getStudentsCount();
    }

    @GetMapping("/average-age")     // Get http://localhost:8080/students/average-age/
    public Double getAverageAge() {
        return studentService.getAverageAge();
    }

    @GetMapping("/last-five")   // Get http://localhost:8080/students/last-five/
    public List<Student> getLastFiveStudents() {
        return studentService.getLastFiveStudents();
    }

    @GetMapping("/a-names")
    public ResponseEntity<List<String>> getNamesStartingWithA() {
        List<String> names = studentService.getNameStudentWithA();
        return ResponseEntity.ok(names);
    }

    @GetMapping("/sum")
    public ResponseEntity<Long> getSum() {
        long n = 1_000_000L;
        long sum = n * (n + 1) / 2;
        return ResponseEntity.ok(sum);
    }

}