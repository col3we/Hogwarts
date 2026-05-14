package com.example.hogwarts.demo.service;

import com.example.hogwarts.demo.model.Faculty;
import com.example.hogwarts.demo.repository.FacultyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service
public class FacultyService {

    private final FacultyRepository facultyRepository;
    private static final Logger logger = LoggerFactory.getLogger(FacultyService.class);

    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
        logger.info("Вызван конструктор FacultyService");
    }

    public Faculty createFaculty(Faculty faculty) {
        logger.info("Вызван метод создания факультета: {}", faculty.getName());
        logger.debug("Сохраняем новый факультет в БД: {}", faculty);

        Faculty savedFaculty = facultyRepository.save(faculty);
        logger.info("Факультет успешно создан с ID: {}", savedFaculty.getId());
        return savedFaculty;
    }

    public Faculty findFaculty(long id) {
        logger.info("Вызван метод поиска факультета по ID: {}", id);
        logger.debug("Ищем факультет с ID = {}", id);

        Faculty faculty = facultyRepository.findById(id).orElse(null);
        if (faculty != null) {
            logger.debug("Факультет найден: {}", faculty.getName());
        } else {
            logger.warn("Факультет с ID {} не найден", id);
        }
        return faculty;
    }

    public Faculty editFaculty(Faculty faculty) {
        logger.info("Вызван метод редактирования факультета ID: {}", faculty.getId());

        if (!facultyRepository.existsById(faculty.getId())) {
            logger.error("Факультет с ID {} не существует для редактирования", faculty.getId());
            return null;
        }

        logger.debug("Обновляем факультет: {}", faculty);
        Faculty updatedFaculty = facultyRepository.save(faculty);
        logger.info("Факультет ID {} успешно обновлён", updatedFaculty.getId());
        return updatedFaculty;
    }

    public Faculty deleteFaculty(long id) {
        logger.info("Вызван метод удаления факультета ID: {}", id);

        Faculty faculty = findFaculty(id);
        if (faculty != null) {
            logger.debug("Удаляем факультет: {}", faculty.getName());
            facultyRepository.deleteById(id);
            logger.info("Факультет ID {} успешно удалён", id);
        } else {
            logger.warn("Попытка удалить несуществующий факультет ID: {}", id);
        }
        return faculty;
    }
}