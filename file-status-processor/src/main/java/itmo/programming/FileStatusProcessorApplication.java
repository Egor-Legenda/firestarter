package itmo.programming;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Главный класс приложения для обработки статусов файлов.
 */
@SpringBootApplication
public class FileStatusProcessorApplication {
    public static void main(String[] args) {
        SpringApplication.run(FileStatusProcessorApplication.class, args);
    }
}