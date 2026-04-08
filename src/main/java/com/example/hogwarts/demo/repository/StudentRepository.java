package com.example.hogwarts.demo.repository;

import com.example.hogwarts.demo.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Collection<Student> findByAge(int age);
    Collection<Student> findByAgeBetween(int age1, int age2);

    @Query("SELECT COUNT(s) FROM Student s")
    long getStudentsCount();

    @Query("SELECT AVG(s.age) FROM Student s")
    Double getAverageAge();

    @Query("SELECT s FROM Student s ORDER BY s.id DESC")
    List<Student> findTop5ByOrderByIdDesc(Pageable pageable);
}