package itmo.programming.model;


import java.time.LocalDateTime;
import itmo.programming.common.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Класс, представляющий статус файла в базе данных MongoDB.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "file_statuses")
public class FileStatus {

    @Id
    private String id;
    private String fileHash;
    private Status status;
    private String fileName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String errorMessage;

    /**
     * Конструктор, инициализирующий основные поля и устанавливающий createdAt и updatedAt текущим временем.
     *
     * @param fileHash Хеш файла.
     * @param status   Статус файла.
     * @param fileName Имя файла.
     */
    public FileStatus(String fileHash, Status status, String fileName) {
        this.fileHash = fileHash;
        this.status = status;
        this.fileName = fileName;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}
