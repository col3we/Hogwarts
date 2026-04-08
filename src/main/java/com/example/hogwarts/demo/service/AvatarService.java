package com.example.hogwarts.demo.service;

import com.example.hogwarts.demo.model.Avatar;
import com.example.hogwarts.demo.model.Student;
import com.example.hogwarts.demo.repository.AvatarRepository;
import jakarta.transaction.Transactional;
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
import java.nio.file.StandardOpenOption;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

@Service
@Transactional
public class AvatarService {
    @Value("avatars")
    private String avatarsDir;

    private final StudentService studentService;
    private final AvatarRepository avatarRepository;

    public AvatarService(StudentService studentService, AvatarRepository avatarRepository) {
        this.studentService = studentService;
        this.avatarRepository = avatarRepository;
    }

    public Avatar findByStudentId(long studentId) {
        return avatarRepository.findByStudentId(studentId).orElse(null);
    }

    private String getExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "jpg";
        }
        return fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
    }

    public void uploadAvatar(Long avatarId, MultipartFile file) throws IOException {
        Student student = studentService.findStudent(avatarId);
        if (student == null) {
            throw new IllegalStateException("Student with id " + avatarId + " not found");
        }

        String ext = getExtension(file.getOriginalFilename());
        Path filePath = Path.of(avatarsDir, student.getId() + "." + ext);
        Files.createDirectories(filePath.getParent());
        Files.deleteIfExists(filePath);

        try (InputStream is = file.getInputStream();
             OutputStream os = Files.newOutputStream(filePath, CREATE_NEW)) {
            is.transferTo(os);
        }

        Avatar avatar = avatarRepository.findByStudentId(avatarId).orElse(new Avatar());

        avatar.setStudent(student);
        avatar.setFilePath(filePath.toString());
        avatar.setFileSize(file.getSize());
        avatar.setMediaType(file.getContentType());
        avatar.setData(generateAvatar(filePath));

        avatarRepository.save(avatar);
    }

    private byte[] generateAvatar(Path filePath)  throws IOException {
        try (InputStream is = Files.newInputStream(filePath)) {
            BufferedImage image = ImageIO.read(is);
            if (image == null) {
                return new byte[0];
            }

            int width = 100;
            int height = (int) (width * (double) image.getWidth() / (double) image.getHeight());
            BufferedImage preview = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = preview.createGraphics();
            graphics.drawImage(image, 0, 0, width, height, null);
            graphics.dispose();

            try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
                ImageIO.write(preview, "jpg", os);
                return os.toByteArray();
            }
        }
    }
}