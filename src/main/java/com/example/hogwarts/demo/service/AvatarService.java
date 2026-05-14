package com.example.hogwarts.demo.service;

import com.example.hogwarts.demo.model.Avatar;
import com.example.hogwarts.demo.model.Student;
import com.example.hogwarts.demo.repository.AvatarRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

@Service
@Transactional
public class AvatarService {
    @Value("avatars")
    private String avatarsDir;

    private final StudentService studentService;
    private final AvatarRepository avatarRepository;

    private static final Logger logger = LoggerFactory.getLogger(AvatarService.class);

    public AvatarService(StudentService studentService, AvatarRepository avatarRepository) {
        this.studentService = studentService;
        this.avatarRepository = avatarRepository;
        logger.info("Вызван конструктор AvatarService");
    }

    public Avatar findByStudentId(long studentId) {
        logger.info("Вызван метод findByStudentId с studentId = {}", studentId);
        logger.debug("Поиск аватара для студента ID: {}", studentId);

        Avatar avatar = avatarRepository.findByStudentId(studentId).orElse(null);
        logger.debug("Аватар найден: {}", avatar != null ? "существует" : "не найден");

        return avatar;
    }

    private String getExtension(String fileName) {
        logger.debug("Вызван метод getExtension, fileName = {}", fileName);

        if (fileName == null || !fileName.contains(".")) {
            logger.warn("Имя файла пустое или без расширения, используется 'jpg'");
            return "jpg";
        }

        String ext = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
        logger.debug("Извлечено расширение файла: {}", ext);
        return ext;
    }

    public void uploadAvatar(Long avatarId, MultipartFile file) throws IOException {
        logger.info("Вызван метод uploadAvatar: studentId={}, fileName={}",
                avatarId, file != null ? file.getOriginalFilename() : "null");

        try {
            logger.debug("Поиск студента ID: {}", avatarId);
            Student student = studentService.findStudent(avatarId);
            if (student == null) {
                logger.error("Студент с ID {} не найден", avatarId);
                throw new IllegalStateException("Студент с id " + avatarId + " не найден");
            }

            logger.debug("Студент найден: {}", student.getId());

            String ext = getExtension(file.getOriginalFilename());
            Path filePath = Path.of(avatarsDir, student.getId() + "." + ext);

            logger.info("Сохранение аватара в: {}", filePath);
            logger.debug("Создание директорий: {}", filePath.getParent());
            Files.createDirectories(filePath.getParent());

            logger.debug("Удаление существующего файла: {}", filePath);
            Files.deleteIfExists(filePath);

            logger.debug("Передача файла, размер: {} байт", file.getSize());
            try (InputStream is = file.getInputStream();
                 OutputStream os = Files.newOutputStream(filePath, CREATE_NEW)) {
                is.transferTo(os);
            }
            logger.info("Файл аватара успешно сохранён: {}", filePath);

            Avatar avatar = avatarRepository.findByStudentId(avatarId).orElse(new Avatar());
            logger.debug("Подготовка сущности аватара: {}", avatar.getId() != null ? "существующая" : "новая");

            avatar.setStudent(student);
            avatar.setFilePath(filePath.toString());
            avatar.setFileSize(file.getSize());
            avatar.setMediaType(file.getContentType());

            logger.debug("Генерация превью аватара из: {}", filePath);
            avatar.setData(generateAvatar(filePath));

            logger.debug("Сохранение аватара в БД");
            avatarRepository.save(avatar);
            logger.info("Аватар успешно сохранён для студента ID: {}", avatarId);

        } catch (IOException e) {
            logger.error("Ошибка ввода-вывода при загрузке аватара для студента {}: {}", avatarId, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Неожиданная ошибка при загрузке аватара для студента {}: {}", avatarId, e.getMessage(), e);
            throw e;
        }
    }

    private byte[] generateAvatar(Path filePath) throws IOException {
        logger.debug("Вызван метод generateAvatar для файла: {}", filePath);

        try (InputStream is = Files.newInputStream(filePath)) {
            logger.debug("Чтение оригинального изображения: {}", filePath);
            BufferedImage image = ImageIO.read(is);

            if (image == null) {
                logger.warn("Не удалось прочитать изображение из файла: {}", filePath);
                return new byte[0];
            }

            logger.debug("Размер оригинала: {}x{}", image.getWidth(), image.getHeight());

            int width = 100;
            int height = (int) (width * (double) image.getWidth() / (double) image.getHeight());
            logger.debug("Генерация превью: {}x{}", width, height);

            BufferedImage preview = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = preview.createGraphics();
            graphics.drawImage(image, 0, 0, width, height, null);
            graphics.dispose();

            logger.debug("Кодирование превью в JPG");
            try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
                ImageIO.write(preview, "jpg", os);
                byte[] data = os.toByteArray();
                logger.debug("Превью сгенерировано, размер: {} байт", data.length);
                return data;
            }
        } catch (IOException e) {
            logger.error("Ошибка генерации превью для файла {}: {}", filePath, e.getMessage(), e);
            throw e;
        }
    }
}