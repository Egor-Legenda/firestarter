package itmo.programming.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Класс, представляющий событие файла для передачи через Kafka.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileEvent {
    private String fileHash;
    private String fileName;
    private byte[] fileContent;
    private LocalDateTime timestamp;

    /**
     * Конструктор, инициализирующий все поля, кроме timestamp, который устанавливается текущим временем.
     *
     * @param fileHash    Хэш файла.
     * @param fileName    Имя файла.
     * @param fileContent Содержимое файла в виде массива байт.
     */
    public FileEvent(String fileHash, String fileName, byte[] fileContent) {
        this.fileHash = fileHash;
        this.fileName = fileName;
        this.fileContent = fileContent;
        this.timestamp = LocalDateTime.now();
    }

}