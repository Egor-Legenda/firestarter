package common;

import itmo.programming.common.StatusEvent;
import itmo.programming.common.Status;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.*;

class StatusEventTest {

    @Test
    @DisplayName("Конструктор с тремя параметрами должен корректно инициализировать поля")
    void threeArgConstructor_ShouldInitializeFieldsCorrectly() {
        // Given
        String fileHash = "abc123";
        Status status = Status.PRIMARY_VALIDATION_SUCCESS;
        String fileName = "test.txt";

        // When
        StatusEvent statusEvent = new StatusEvent(fileHash, status, fileName);

        // Then
        assertThat(statusEvent.getFileHash()).isEqualTo(fileHash);
        assertThat(statusEvent.getStatus()).isEqualTo(status);
        assertThat(statusEvent.getFileName()).isEqualTo(fileName);
        assertThat(statusEvent.getTimestamp()).isNotNull();
        assertThat(statusEvent.getTimestamp()).isBeforeOrEqualTo(LocalDateTime.now());
        assertThat(statusEvent.getErrorMessage()).isNull();
    }

    @Test
    @DisplayName("Конструктор с тремя параметрами должен устанавливать текущее время")
    void threeArgConstructor_ShouldSetCurrentTimestamp() {
        // Given
        LocalDateTime beforeCreation = LocalDateTime.now().minusSeconds(1);

        // When
        StatusEvent statusEvent = new StatusEvent("hash", Status.SECONDARY_VALIDATION_STARTED, "file.txt");

        // Then
        LocalDateTime afterCreation = LocalDateTime.now().plusSeconds(1);
        assertThat(statusEvent.getTimestamp())
                .isAfter(beforeCreation)
                .isBefore(afterCreation);
    }

    @Test
    @DisplayName("Конструктор без параметров должен создавать объект с null полями")
    void noArgConstructor_ShouldCreateObjectWithNullFields() {
        // When
        StatusEvent statusEvent = new StatusEvent();

        // Then
        assertThat(statusEvent.getFileHash()).isNull();
        assertThat(statusEvent.getStatus()).isNull();
        assertThat(statusEvent.getFileName()).isNull();
        assertThat(statusEvent.getTimestamp()).isNull();
        assertThat(statusEvent.getErrorMessage()).isNull();
    }

    @Test
    @DisplayName("Конструктор со всеми параметрами должен корректно инициализировать все поля")
    void allArgsConstructor_ShouldInitializeAllFields() {
        // Given
        String fileHash = "def456";
        Status status = Status.UPLOAD_ERROR;
        String fileName = "document.pdf";
        LocalDateTime timestamp = LocalDateTime.of(2023, 12, 1, 10, 30, 0);
        String errorMessage = "File not found";

        // When
        StatusEvent statusEvent = new StatusEvent(fileHash, status, fileName, timestamp, errorMessage);

        // Then
        assertThat(statusEvent.getFileHash()).isEqualTo(fileHash);
        assertThat(statusEvent.getStatus()).isEqualTo(status);
        assertThat(statusEvent.getFileName()).isEqualTo(fileName);
        assertThat(statusEvent.getTimestamp()).isEqualTo(timestamp);
        assertThat(statusEvent.getErrorMessage()).isEqualTo(errorMessage);
    }

    @Test
    @DisplayName("Setter'ы должны корректно устанавливать значения")
    void setters_ShouldSetValuesCorrectly() {
        // Given
        StatusEvent statusEvent = new StatusEvent();
        String fileHash = "newHash";
        Status status = Status.SECONDARY_VALIDATION_SUCCESS;
        String fileName = "newFile.txt";
        LocalDateTime timestamp = LocalDateTime.now();
        String errorMessage = "Custom error message";

        // When
        statusEvent.setFileHash(fileHash);
        statusEvent.setStatus(status);
        statusEvent.setFileName(fileName);
        statusEvent.setTimestamp(timestamp);
        statusEvent.setErrorMessage(errorMessage);

        // Then
        assertThat(statusEvent.getFileHash()).isEqualTo(fileHash);
        assertThat(statusEvent.getStatus()).isEqualTo(status);
        assertThat(statusEvent.getFileName()).isEqualTo(fileName);
        assertThat(statusEvent.getTimestamp()).isEqualTo(timestamp);
        assertThat(statusEvent.getErrorMessage()).isEqualTo(errorMessage);
    }

    @Test
    @DisplayName("Два объекта с одинаковыми полями должны быть равны")
    void equals_ShouldReturnTrueForSameFields() {
        // Given
        LocalDateTime timestamp = LocalDateTime.now();
        String fileHash = "hash123";
        Status status = Status.SECONDARY_VALIDATION_STARTED;
        String fileName = "file.txt";
        String errorMessage = "Error occurred";

        StatusEvent event1 = new StatusEvent(fileHash, status, fileName, timestamp, errorMessage);
        StatusEvent event2 = new StatusEvent(fileHash, status, fileName, timestamp, errorMessage);

        // Then
        assertThat(event1).isEqualTo(event2);
        assertThat(event1.hashCode()).isEqualTo(event2.hashCode());
    }

    @Test
    @DisplayName("Два объекта с разными полями не должны быть равны")
    void equals_ShouldReturnFalseForDifferentFields() {
        // Given
        LocalDateTime timestamp1 = LocalDateTime.now();
        LocalDateTime timestamp2 = LocalDateTime.now().plusHours(1);

        StatusEvent event1 = new StatusEvent("hash1", Status.PRIMARY_VALIDATION_SUCCESS, "file1.txt", timestamp1, null);
        StatusEvent event2 = new StatusEvent("hash2", Status.SECONDARY_VALIDATION_ERROR, "file2.txt", timestamp2, "File not found");

        // Then
        assertThat(event1).isNotEqualTo(event2);
        assertThat(event1.hashCode()).isNotEqualTo(event2.hashCode());
    }

    @Test
    @DisplayName("Метод toString должен возвращать непустую строку")
    void toString_ShouldReturnNonEmptyString() {
        // Given
        StatusEvent statusEvent = new StatusEvent("hash123", Status.SECONDARY_VALIDATION_SUCCESS, "test.txt");

        // When
        String result = statusEvent.toString();

        // Then
        assertThat(result).isNotBlank();
        assertThat(result).contains("hash123", "SUCCESS", "test.txt");
    }

    @Test
    @DisplayName("Работа с null значениями в полях")
    void nullValues_ShouldBeHandledCorrectly() {
        // When
        StatusEvent statusEvent = new StatusEvent(null, null, null, null, null);

        // Then
        assertThat(statusEvent.getFileHash()).isNull();
        assertThat(statusEvent.getStatus()).isNull();
        assertThat(statusEvent.getFileName()).isNull();
        assertThat(statusEvent.getTimestamp()).isNull();
        assertThat(statusEvent.getErrorMessage()).isNull();
    }

    @Test
    @DisplayName("Проверка работы с пустым errorMessage")
    void emptyErrorMessage_ShouldBeHandledCorrectly() {
        // Given
        String emptyMessage = "";

        // When
        StatusEvent statusEvent = new StatusEvent("hash", Status.UPLOAD_ERROR, "file.txt", LocalDateTime.now(), emptyMessage);

        // Then
        assertThat(statusEvent.getErrorMessage()).isEmpty();
        assertThat(statusEvent.getErrorMessage()).isEqualTo("");
    }

    @Test
    @DisplayName("Проверка работы с различными статусами")
    void differentStatuses_ShouldBeHandledCorrectly() {
        // Given
        Status successStatus = Status.SECONDARY_VALIDATION_SUCCESS;
        Status errorStatus = Status.SECONDARY_VALIDATION_FAILED;
        Status processingStatus = Status.SECONDARY_VALIDATION_STARTED;

        // When
        StatusEvent successEvent = new StatusEvent("hash1", successStatus, "file1.txt");
        StatusEvent errorEvent = new StatusEvent("hash2", errorStatus, "file2.txt", LocalDateTime.now(), "Error");
        StatusEvent processingEvent = new StatusEvent("hash3", processingStatus, "file3.txt");

        // Then
        assertThat(successEvent.getStatus()).isEqualTo(Status.SECONDARY_VALIDATION_SUCCESS);
        assertThat(errorEvent.getStatus()).isEqualTo(Status.SECONDARY_VALIDATION_FAILED);
        assertThat(processingEvent.getStatus()).isEqualTo(Status.SECONDARY_VALIDATION_STARTED);
    }

    @Test
    @DisplayName("Проверка корректности установки errorMessage для статуса ERROR")
    void errorStatusWithMessage_ShouldBeHandledCorrectly() {
        // Given
        String errorMessage = "File processing failed due to invalid format";

        // When
        StatusEvent statusEvent = new StatusEvent("hash", Status.SECONDARY_VALIDATION_ERROR, "file.txt", LocalDateTime.now(), errorMessage);

        // Then
        assertThat(statusEvent.getStatus()).isEqualTo(Status.SECONDARY_VALIDATION_ERROR);
        assertThat(statusEvent.getErrorMessage()).isEqualTo(errorMessage);
        assertThat(statusEvent.getErrorMessage()).contains("failed");
    }

    @Test
    @DisplayName("Проверка иммутабельности строковых полей")
    void stringFields_ShouldNotBeModifiedExternally() {
        // Given
        String originalHash = "originalHash";
        String originalFileName = "originalFile.txt";
        String originalErrorMessage = "original error";

        StatusEvent statusEvent = new StatusEvent(originalHash, Status.PRIMARY_VALIDATION_SUCCESS, originalFileName,
                LocalDateTime.now(), originalErrorMessage);

        // When - изменяем оригинальные строки
        originalHash = "modifiedHash";
        originalFileName = "modifiedFile.txt";
        originalErrorMessage = "modified error";

        // Then - поля объекта не должны измениться
        assertThat(statusEvent.getFileHash()).isEqualTo("originalHash");
        assertThat(statusEvent.getFileName()).isEqualTo("originalFile.txt");
        assertThat(statusEvent.getErrorMessage()).isEqualTo("original error");
    }
}
