package itmo.programming.exception;

/**
 * Исключение, выбрасываемое при ошибках валидации файлов.
 */
public class FileValidationException extends RuntimeException {
    /**
     * Конструктор исключения с сообщением об ошибке.
     *
     * @param message Сообщение об ошибке.
     */
    public FileValidationException(String message) {
        super(message);
    }

    /**
     * Конструктор исключения с сообщением об ошибке и причиной.
     *
     * @param message Сообщение об ошибке.
     * @param cause   Причина исключения.
     */
    public FileValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Конструктор исключения с причиной.
     */
    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
