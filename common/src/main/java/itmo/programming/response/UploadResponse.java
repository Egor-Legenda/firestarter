package itmo.programming.response;

import lombok.Data;

/**
 * Класс, представляющий ответ с информацией об ошибке.
 */
@Data
public class UploadResponse {
    private String fileHash;
    private String status;

    /**
     * Конструктор, инициализирующий все поля.
     *
     * @param fileHash Хэш файла.
     * @param status   Статус обработки файла.
     */
    public UploadResponse(String fileHash, String status) {
        this.fileHash = fileHash;
        this.status = status;
    }
}