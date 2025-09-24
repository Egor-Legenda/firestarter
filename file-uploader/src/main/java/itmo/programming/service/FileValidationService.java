package itmo.programming.service;

import itmo.programming.exception.FileValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Сервис для валидации загружаемых файлов.
 */
@Slf4j
@Service
public class FileValidationService {

    @Value("${app.upload.max-file-size:5MB}")
    private String maxFileSize;

    @Value("${app.upload.allowed-extensions}")
    private String[] allowedExtensions;

    /**
     * Валидирует файл по размеру и расширению.
     *
     * @param file загружаемый файл
     * @throws FileValidationException если файл не прошел валидацию
     */
    public void validateFile(MultipartFile file) {
        validateFileSize(file);
        validateFileExtension(file);
    }

    /**
     * Проверяет размер файла
     *
     * @param file загружаемый файл
     * @throws FileValidationException если размер превышает допустимый
     */
    private void validateFileSize(MultipartFile file) {
        long maxSize = parseSize(maxFileSize);
        if (file.getSize() > maxSize) {
            throw new FileValidationException("File size exceeds limit");
        }
    }

    /**
     * Проверяет расширение файла
     *
     * @param file загружаемый файл
     * @throws FileValidationException если расширение недопустимо
     */
    private void validateFileExtension(MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (filename == null || !hasValidExtension(filename)) {
            throw new FileValidationException("Invalid file extension");
        }
    }

    /**
     * Проверяет, есть ли у файла допустимое расширение
     *
     * @param filename имя файла
     * @return true, если расширение допустимо, иначе false
     */
    private boolean hasValidExtension(String filename) {
        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        for (String allowed : allowedExtensions) {
            if (allowed.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Парсит строковое представление размера файла (например, "5MB") в байты.
     * Поддерживаются суффиксы KB, MB, GB.
     *
     * @param size строковое представление размера
     * @return размер в байтах
     */
    private long parseSize(String size) {
        long sizeInt = Long.parseLong(size.split("")[0]);
        return sizeInt * 1024 * 1024;
    }
}