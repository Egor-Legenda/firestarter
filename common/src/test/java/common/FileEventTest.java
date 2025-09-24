package common;

import itmo.programming.common.FileEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.time.LocalDateTime;
import java.util.Arrays;
import static org.assertj.core.api.Assertions.*;

class FileEventTest {

    @Test
    @DisplayName("Конструктор с тремя параметрами должен корректно инициализировать поля")
    void threeArgConstructor_ShouldInitializeFieldsCorrectly() {
        // Given
        String fileHash = "abc123";
        String fileName = "test.txt";
        byte[] fileContent = new byte[]{1, 2, 3, 4, 5};

        // When
        FileEvent fileEvent = new FileEvent(fileHash, fileName, fileContent);

        // Then
        assertThat(fileEvent.getFileHash()).isEqualTo(fileHash);
        assertThat(fileEvent.getFileName()).isEqualTo(fileName);
        assertThat(fileEvent.getFileContent()).isEqualTo(fileContent);
        assertThat(fileEvent.getTimestamp()).isNotNull();
        assertThat(fileEvent.getTimestamp()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    @DisplayName("Конструктор с тремя параметрами должен устанавливать текущее время")
    void threeArgConstructor_ShouldSetCurrentTimestamp() {
        // Given
        LocalDateTime beforeCreation = LocalDateTime.now().minusSeconds(1);

        // When
        FileEvent fileEvent = new FileEvent("hash", "file.txt", new byte[]{1, 2, 3});

        // Then
        LocalDateTime afterCreation = LocalDateTime.now().plusSeconds(1);
        assertThat(fileEvent.getTimestamp())
                .isAfter(beforeCreation)
                .isBefore(afterCreation);
    }

    @Test
    @DisplayName("Конструктор без параметров должен создавать объект с null полями")
    void noArgConstructor_ShouldCreateObjectWithNullFields() {
        // When
        FileEvent fileEvent = new FileEvent();

        // Then
        assertThat(fileEvent.getFileHash()).isNull();
        assertThat(fileEvent.getFileName()).isNull();
        assertThat(fileEvent.getFileContent()).isNull();
        assertThat(fileEvent.getTimestamp()).isNull();
    }

    @Test
    @DisplayName("Конструктор со всеми параметрами должен корректно инициализировать все поля")
    void allArgsConstructor_ShouldInitializeAllFields() {
        // Given
        String fileHash = "def456";
        String fileName = "document.pdf";
        byte[] fileContent = new byte[]{10, 20, 30};
        LocalDateTime timestamp = LocalDateTime.of(2023, 12, 1, 10, 30, 0);

        // When
        FileEvent fileEvent = new FileEvent(fileHash, fileName, fileContent, timestamp);

        // Then
        assertThat(fileEvent.getFileHash()).isEqualTo(fileHash);
        assertThat(fileEvent.getFileName()).isEqualTo(fileName);
        assertThat(fileEvent.getFileContent()).isEqualTo(fileContent);
        assertThat(fileEvent.getTimestamp()).isEqualTo(timestamp);
    }

    @Test
    @DisplayName("Setter'ы должны корректно устанавливать значения")
    void setters_ShouldSetValuesCorrectly() {
        // Given
        FileEvent fileEvent = new FileEvent();
        String fileHash = "newHash";
        String fileName = "newFile.txt";
        byte[] fileContent = new byte[]{100, (byte) 200};
        LocalDateTime timestamp = LocalDateTime.now();

        // When
        fileEvent.setFileHash(fileHash);
        fileEvent.setFileName(fileName);
        fileEvent.setFileContent(fileContent);
        fileEvent.setTimestamp(timestamp);

        // Then
        assertThat(fileEvent.getFileHash()).isEqualTo(fileHash);
        assertThat(fileEvent.getFileName()).isEqualTo(fileName);
        assertThat(fileEvent.getFileContent()).isEqualTo(fileContent);
        assertThat(fileEvent.getTimestamp()).isEqualTo(timestamp);
    }

    @Test
    @DisplayName("Два объекта с одинаковыми полями должны быть равны")
    void equals_ShouldReturnTrueForSameFields() {
        // Given
        LocalDateTime timestamp = LocalDateTime.now();
        byte[] content = new byte[]{1, 2, 3};

        FileEvent event1 = new FileEvent("hash", "file.txt", content, timestamp);
        FileEvent event2 = new FileEvent("hash", "file.txt", content, timestamp);

        // Then
        assertThat(event1).isEqualTo(event2);
        assertThat(event1.hashCode()).isEqualTo(event2.hashCode());
    }

    @Test
    @DisplayName("Два объекта с разными полями не должны быть равны")
    void equals_ShouldReturnFalseForDifferentFields() {
        // Given
        FileEvent event1 = new FileEvent("hash1", "file1.txt", new byte[]{1}, LocalDateTime.now());
        FileEvent event2 = new FileEvent("hash2", "file2.txt", new byte[]{2}, LocalDateTime.now().plusHours(1));

        // Then
        assertThat(event1).isNotEqualTo(event2);
        assertThat(event1.hashCode()).isNotEqualTo(event2.hashCode());
    }

    @Test
    @DisplayName("Метод toString должен возвращать непустую строку")
    void toString_ShouldReturnNonEmptyString() {
        // Given
        FileEvent fileEvent = new FileEvent("hash", "test.txt", new byte[]{1, 2, 3});

        // When
        String result = fileEvent.toString();

        // Then
        assertThat(result).isNotBlank();
        assertThat(result).contains("hash", "test.txt");
    }

    @Test
    @DisplayName("Работа с null значениями в полях")
    void nullValues_ShouldBeHandledCorrectly() {
        // When
        FileEvent fileEvent = new FileEvent(null, null, null, null);

        // Then
        assertThat(fileEvent.getFileHash()).isNull();
        assertThat(fileEvent.getFileName()).isNull();
        assertThat(fileEvent.getFileContent()).isNull();
        assertThat(fileEvent.getTimestamp()).isNull();
    }

    @Test
    @DisplayName("Проверка работы с пустым fileContent")
    void emptyFileContent_ShouldBeHandledCorrectly() {
        // Given
        byte[] emptyContent = new byte[0];

        // When
        FileEvent fileEvent = new FileEvent("hash", "empty.txt", emptyContent);

        // Then
        assertThat(fileEvent.getFileContent()).isEmpty();
        assertThat(fileEvent.getFileContent()).hasSize(0);
    }

    @Test
    @DisplayName("Проверка иммутабельности fileContent")
    void fileContent_ShouldNotBeModifiedExternally() {
        // Given
        byte[] originalContent = new byte[]{1, 2, 3};
        FileEvent fileEvent = new FileEvent("hash", "file.txt", originalContent);

        // When
        originalContent[0] = 100;

        // Then
        assertThat(fileEvent.getFileContent()).isEqualTo(new byte[]{100, 2, 3});
    }
}