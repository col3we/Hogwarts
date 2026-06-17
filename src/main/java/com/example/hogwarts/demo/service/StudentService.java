package com.example.hogwarts.demo.service;

import com.example.hogwarts.demo.model.Student;
import com.example.hogwarts.demo.repository.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
        logger.info("Вызван конструктор StudentService");
    }

    public Student createStudent(Student student) {
        logger.info("Вызван метод создания студента: {}", student.getName());
        logger.debug("Сохраняем нового студента в БД: {}", student);

        Student savedStudent = studentRepository.save(student);
        logger.info("Студент успешно создан с ID: {}", savedStudent.getId());
        return savedStudent;
    }

    public Student findStudent(Long id) {
        logger.info("Вызван метод поиска студента по ID: {}", id);
        logger.debug("Ищем студента с ID = {}", id);

        Student student = studentRepository.findById(id).orElse(null);
        if (student != null) {
            logger.debug("Студент найден: {}", student.getName());
        } else {
            logger.warn("Студент с ID {} не найден", id);
        }
        return student;
    }

    public Student editStudent(Student student) {
        logger.info("Вызван метод редактирования студента ID: {}", student.getId());

        if (!studentRepository.existsById(student.getId())) {
            logger.error("Студент с ID {} не существует для редактирования", student.getId());
            return null;
        }

        logger.debug("Обновляем студента: {}", student);
        Student updatedStudent = studentRepository.save(student);
        logger.info("Студент ID {} успешно обновлён", updatedStudent.getId());
        return updatedStudent;
    }

    public void deleteStudent(Long id) {
        logger.info("Вызван метод удаления студента ID: {}", id);
        logger.debug("Проверяем существование студента ID: {}", id);

        if (!studentRepository.existsById(id)) {
            logger.warn("Попытка удалить несуществующего студента ID: {}", id);
            return;
        }

        logger.debug("Удаляем студента ID: {}", id);
        studentRepository.deleteById(id);
        logger.info("Студент ID {} успешно удалён", id);
    }

    public Collection<Student> getAllStudents() {
        logger.info("Вызван метод получения всех студентов");
        logger.debug("Загружаем список всех студентов из БД");

        Collection<Student> students = studentRepository.findAll();
        logger.info("Загружено студентов: {}", students.size());
        logger.debug("Общее количество студентов: {}", students.size());

        return students;
    }

    public Collection<Student> findByAge(int age) {
        logger.info("Вызван метод поиска студентов по возрасту: {}", age);
        logger.debug("Ищем студентов с возрастом = {}", age);

        Collection<Student> students = studentRepository.findByAge(age);
        logger.info("Найдено студентов с возрастом {}: {}", age, students.size());
        return students;
    }

    public Collection<Student> findByAgeBetween(int min, int max) {
        logger.info("Вызван метод поиска студентов по возрасту от {} до {}", min, max);
        logger.debug("Ищем студентов в диапазоне возраста [{}, {}]", min, max);

        Collection<Student> students = studentRepository.findByAgeBetween(min, max);
        logger.info("Найдено студентов в диапазоне [{}, {}]: {}", min, max, students.size());
        return students;
    }

    public long getStudentsCount() {
        logger.info("Вызван метод подсчёта общего количества студентов");
        logger.debug("Выполняем запрос на подсчёт студентов");

        long count = studentRepository.getStudentsCount();
        logger.info("Общее количество студентов: {}", count);
        return count;
    }

    public Double getAverageAge() {
        logger.info("Вызван метод получения среднего возраста студентов");
        logger.debug("Выполняем запрос на средний возраст");

        Double averageAge = studentRepository.getAverageAge();
        logger.info("Средний возраст студентов: {}", averageAge);
        logger.debug("Средний возраст: {}", averageAge);
        return averageAge;
    }

    public List<Student> getLastFiveStudents() {
        logger.info("Вызван метод получения последних 5 студентов");
        logger.debug("Загружаем топ-5 последних студентов по ID DESC");

        List<Student> lastStudents = studentRepository.findTop5ByOrderByIdDesc(PageRequest.of(0, 5));
        logger.info("Загружено последних студентов: {}", lastStudents.size());
        logger.debug("Последние студенты: {}", lastStudents.stream().map(Student::getName).toList());

        return lastStudents;
    }

    public List<String> getNameStudentWithA() {
        return studentRepository.findAll().stream()
                .map(Student::getName)
                .filter(name -> name != null && name.toUpperCase().startsWith("A"))
                .map(String::toUpperCase)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
}