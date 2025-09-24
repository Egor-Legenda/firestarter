package itmo.programming.response;

import lombok.Data;

/**
 * Класс, представляющий ответ с информацией об ошибке.
 */
@Data
public class ErrorResponse {
    private String errorType;
    private String message;

    /**
     * Конструктор, инициализирующий поля ошибки.
     *
     * @param errorType Тип ошибки.
     * @param message   Сообщение об ошибке.
     */
    public ErrorResponse(String errorType, String message) {
        this.errorType = errorType;
        this.message = message;
    }
}
