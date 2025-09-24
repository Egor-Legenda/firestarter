package exception;

import itmo.programming.exception.FileValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.io.IOException;

import static org.assertj.core.api.Assertions.*;

class FileValidationExceptionTest {

    @Test
    @DisplayName("Конструктор с сообщением должен корректно инициализировать исключение")
    void constructorWithMessage_ShouldInitializeCorrectly() {
        // Given
        String errorMessage = "File validation failed";

        // When
        FileValidationException exception = new FileValidationException(errorMessage);

        // Then
        assertThat(exception).isInstanceOf(RuntimeException.class);
        assertThat(exception.getMessage()).isEqualTo(errorMessage);
        assertThat(exception.getCause()).isNull();
    }

    @Test
    @DisplayName("Конструктор с сообщением и причиной должен корректно инициализировать исключение")
    void constructorWithMessageAndCause_ShouldInitializeCorrectly() {
        // Given
        String errorMessage = "File validation failed";
        Throwable cause = new IllegalArgumentException("Invalid file format");

        // When
        FileValidationException exception = new FileValidationException(errorMessage, cause);

        // Then
        assertThat(exception.getMessage()).isEqualTo(errorMessage);
        assertThat(exception.getCause()).isEqualTo(cause);
        assertThat(exception.getCause()).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Исключение должно корректно наследовать поведение RuntimeException")
    void exception_ShouldInheritRuntimeExceptionBehavior() {
        // When
        FileValidationException exception = new FileValidationException("Test error");

        // Then
        assertThat(exception)
                .isInstanceOf(RuntimeException.class)
                .isInstanceOf(Throwable.class);
    }

    @Test
    @DisplayName("Исключение с null сообщением должно обрабатываться корректно")
    void constructorWithNullMessage_ShouldHandleNullCorrectly() {
        // When
        FileValidationException exception = new FileValidationException(null);

        // Then
        assertThat(exception.getMessage()).isNull();
        assertThat(exception.getCause()).isNull();
    }

    @Test
    @DisplayName("Исключение с null причиной должно обрабатываться корректно")
    void constructorWithNullCause_ShouldHandleNullCorrectly() {
        // When
        FileValidationException exception = new FileValidationException("Error", null);

        // Then
        assertThat(exception.getMessage()).isEqualTo("Error");
        assertThat(exception.getCause()).isNull();
    }

    @Test
    @DisplayName("Стек трейс должен быть доступен")
    void stackTrace_ShouldBeAvailable() {
        // When
        FileValidationException exception = new FileValidationException("Error");
        StackTraceElement[] stackTrace = exception.getStackTrace();

        // Then
        assertThat(stackTrace).isNotNull();
        assertThat(exception.toString()).contains("FileValidationException");
        assertThat(exception.toString()).contains("Error");
    }

    @Test
    @DisplayName("Исключение должно быть способно пробрасываться и перехватываться")
    void exception_ShouldBeThrowableAndCatchable() {
        // Given
        String expectedMessage = "Validation failed";

        // When & Then
        assertThatThrownBy(() -> {
            throw new FileValidationException(expectedMessage);
        })
                .isInstanceOf(FileValidationException.class)
                .hasMessage(expectedMessage);
    }

    @Test
    @DisplayName("Исключение с причиной должно сохранять оригинальное исключение")
    void exceptionWithCause_ShouldPreserveOriginalException() {
        // Given
        IOException ioException = new IOException("File not found");

        // When
        FileValidationException exception = new FileValidationException("Processing failed", ioException);

        // Then
        assertThat(exception.getCause())
                .isEqualTo(ioException)
                .hasMessage("File not found");
    }
}