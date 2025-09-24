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
public class StatusEvent {
    private String fileHash;
    private Status status;
    private String fileName;
    private LocalDateTime timestamp;
    private String errorMessage;


    /**
     * Конструктор, инициализирующий все поля, кроме timestamp, который устанавливается текущим временем.
     *
     * @param fileHash Хэш файла.
     * @param status   Статус обработки файла.
     * @param fileName Имя файла.
     */
    public StatusEvent(String fileHash, Status status, String fileName) {
        this.fileHash = fileHash;
        this.status = status;
        this.fileName = fileName;
        this.timestamp = LocalDateTime.now();
    }

}