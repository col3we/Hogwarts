package com.example.hogwarts.demo.controller;

import com.example.hogwarts.demo.model.Student;
import com.example.hogwarts.demo.service.StudentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;


@RestController
@RequestMapping("/students")
public class StudentController {
    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/{id}") // GET http://localhost:8080/students/
    public ResponseEntity<Student> getBookInfo(@PathVariable Long id) {
        Student student = studentService.findStudent(id);
        if (student == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(student);
    }

    @GetMapping  // GET http://localhost:8080/students
    public ResponseEntity<Collection<Student>> getAllBooks() {
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

    @GetMapping("/age/{age}")   // GET http://localhost:8080/students/age/
    public Collection<Student> getStudentsByAge(@PathVariable int age) {
        return studentService.findByAge(age);
    }

    @GetMapping("/age{age}")    // GET http://localhost:8080/students/age/
    public Collection<Student> getStudentsByAgeBetween(@PathVariable int age1, @PathVariable int age2) {
        return studentService.findByAgeBetween(age1, age2);
    }

}
