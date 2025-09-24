package response;

import itmo.programming.response.UploadResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.assertj.core.api.Assertions.*;

class UploadResponseTest {

    @Test
    @DisplayName("Конструктор должен корректно инициализировать поля")
    void constructor_ShouldInitializeFieldsCorrectly() {
        // Given
        String fileHash = "abc123def456";
        String status = "SUCCESS";

        // When
        UploadResponse uploadResponse = new UploadResponse(fileHash, status);

        // Then
        assertThat(uploadResponse.getFileHash()).isEqualTo(fileHash);
        assertThat(uploadResponse.getStatus()).isEqualTo(status);
    }

    @Test
    @DisplayName("Setter'ы должны корректно устанавливать значения")
    void setters_ShouldSetValuesCorrectly() {
        // Given
        UploadResponse uploadResponse = new UploadResponse("oldHash", "PENDING");
        String newFileHash = "newHash789";
        String newStatus = "COMPLETED";

        // When
        uploadResponse.setFileHash(newFileHash);
        uploadResponse.setStatus(newStatus);

        // Then
        assertThat(uploadResponse.getFileHash()).isEqualTo(newFileHash);
        assertThat(uploadResponse.getStatus()).isEqualTo(newStatus);
    }

    @Test
    @DisplayName("Два объекта с одинаковыми полями должны быть равны")
    void equals_ShouldReturnTrueForSameFields() {
        // Given
        UploadResponse response1 = new UploadResponse("hash123", "SUCCESS");
        UploadResponse response2 = new UploadResponse("hash123", "SUCCESS");

        // Then
        assertThat(response1).isEqualTo(response2);
        assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
    }

    @Test
    @DisplayName("Два объекта с разными fileHash не должны быть равны")
    void equals_ShouldReturnFalseForDifferentFileHash() {
        // Given
        UploadResponse response1 = new UploadResponse("hash1", "SUCCESS");
        UploadResponse response2 = new UploadResponse("hash2", "SUCCESS");

        // Then
        assertThat(response1).isNotEqualTo(response2);
        assertThat(response1.hashCode()).isNotEqualTo(response2.hashCode());
    }

    @Test
    @DisplayName("Два объекта с разными status не должны быть равны")
    void equals_ShouldReturnFalseForDifferentStatus() {
        // Given
        UploadResponse response1 = new UploadResponse("hash123", "PENDING");
        UploadResponse response2 = new UploadResponse("hash123", "SUCCESS");

        // Then
        assertThat(response1).isNotEqualTo(response2);
        assertThat(response1.hashCode()).isNotEqualTo(response2.hashCode());
    }

    @Test
    @DisplayName("Объект не должен быть равен null")
    void equals_ShouldReturnFalseForNull() {
        // Given
        UploadResponse uploadResponse = new UploadResponse("hash", "STATUS");

        // Then
        assertThat(uploadResponse).isNotEqualTo(null);
    }

    @Test
    @DisplayName("Объект не должен быть равен объекту другого класса")
    void equals_ShouldReturnFalseForDifferentClass() {
        // Given
        UploadResponse uploadResponse = new UploadResponse("hash", "STATUS");
        String otherObject = "Not an UploadResponse";

        // Then
        assertThat(uploadResponse).isNotEqualTo(otherObject);
    }

    @Test
    @DisplayName("Метод toString должен возвращать непустую строку с информацией об объекте")
    void toString_ShouldReturnNonEmptyStringWithObjectInfo() {
        // Given
        UploadResponse uploadResponse = new UploadResponse("abc123", "UPLOADED");

        // When
        String result = uploadResponse.toString();

        // Then
        assertThat(result).isNotBlank();
        assertThat(result).contains("abc123", "UPLOADED");
        assertThat(result).contains("UploadResponse");
    }

    @Test
    @DisplayName("Работа с null значениями в полях")
    void nullValues_ShouldBeHandledCorrectly() {
        // When
        UploadResponse uploadResponse = new UploadResponse(null, null);

        // Then
        assertThat(uploadResponse.getFileHash()).isNull();
        assertThat(uploadResponse.getStatus()).isNull();
    }

    @Test
    @DisplayName("Setter'ы должны корректно работать с null значениями")
    void setters_ShouldHandleNullValuesCorrectly() {
        // Given
        UploadResponse uploadResponse = new UploadResponse("hash", "status");

        // When
        uploadResponse.setFileHash(null);
        uploadResponse.setStatus(null);

        // Then
        assertThat(uploadResponse.getFileHash()).isNull();
        assertThat(uploadResponse.getStatus()).isNull();
    }

    @Test
    @DisplayName("Проверка работы с пустыми строками")
    void emptyStrings_ShouldBeHandledCorrectly() {
        // Given
        String emptyHash = "";
        String emptyStatus = "";

        // When
        UploadResponse uploadResponse = new UploadResponse(emptyHash, emptyStatus);

        // Then
        assertThat(uploadResponse.getFileHash()).isEmpty();
        assertThat(uploadResponse.getStatus()).isEmpty();
    }

    @Test
    @DisplayName("Проверка иммутабельности строковых полей при внешних изменениях")
    void stringFields_ShouldNotBeAffectedByExternalChanges() {
        // Given
        String originalHash = "originalHash";
        String originalStatus = "originalStatus";
        UploadResponse uploadResponse = new UploadResponse(originalHash, originalStatus);

        // When - изменяем оригинальные строки
        originalHash = "modifiedHash";
        originalStatus = "modifiedStatus";

        // Then - поля объекта не должны измениться
        assertThat(uploadResponse.getFileHash()).isEqualTo("originalHash");
        assertThat(uploadResponse.getStatus()).isEqualTo("originalStatus");
    }

    @Test
    @DisplayName("Создание объекта с различными статусами")
    void objectCreation_WithVariousStatuses() {
        // Given
        String[] statuses = {"PENDING", "UPLOADING", "SUCCESS", "FAILED", "CANCELLED"};

        for (String status : statuses) {
            // When
            UploadResponse uploadResponse = new UploadResponse("hash123", status);

            // Then
            assertThat(uploadResponse.getStatus()).isEqualTo(status);
            assertThat(uploadResponse.getFileHash()).isEqualTo("hash123");
        }
    }

    @Test
    @DisplayName("Создание объекта с различными хэшами файлов")
    void objectCreation_WithVariousFileHashes() {
        // Given
        String[] hashes = {
                "md5_abc123",
                "sha1_def456",
                "sha256_ghi789",
                "very_long_hash_that_contains_many_characters_1234567890"
        };

        for (String hash : hashes) {
            // When
            UploadResponse uploadResponse = new UploadResponse(hash, "SUCCESS");

            // Then
            assertThat(uploadResponse.getFileHash()).isEqualTo(hash);
            assertThat(uploadResponse.getStatus()).isEqualTo("SUCCESS");
        }
    }

    @Test
    @DisplayName("Проверка консистентности equals и hashCode")
    void equalsAndHashCode_ShouldBeConsistent() {
        // Given
        UploadResponse response1 = new UploadResponse("hash", "status");
        UploadResponse response2 = new UploadResponse("hash", "status");

        // When & Then
        assertThat(response1.equals(response2)).isTrue();
        assertThat(response1.hashCode() == response2.hashCode()).isTrue();

        // При изменении объекта хэш-код должен измениться
        response2.setStatus("NEW_STATUS");
        assertThat(response1.equals(response2)).isFalse();
        assertThat(response1.hashCode() == response2.hashCode()).isFalse();
    }

    @Test
    @DisplayName("Рефлексивность equals")
    void equals_ShouldBeReflexive() {
        // Given
        UploadResponse uploadResponse = new UploadResponse("hash", "status");

        // Then
        assertThat(uploadResponse).isEqualTo(uploadResponse);
    }

    @Test
    @DisplayName("Симметричность equals")
    void equals_ShouldBeSymmetric() {
        // Given
        UploadResponse response1 = new UploadResponse("hash", "status");
        UploadResponse response2 = new UploadResponse("hash", "status");

        // Then
        assertThat(response1.equals(response2)).isTrue();
        assertThat(response2.equals(response1)).isTrue();
    }

    @Test
    @DisplayName("Транзитивность equals")
    void equals_ShouldBeTransitive() {
        // Given
        UploadResponse response1 = new UploadResponse("hash", "status");
        UploadResponse response2 = new UploadResponse("hash", "status");
        UploadResponse response3 = new UploadResponse("hash", "status");

        // Then
        assertThat(response1.equals(response2)).isTrue();
        assertThat(response2.equals(response3)).isTrue();
        assertThat(response1.equals(response3)).isTrue();
    }

    @Test
    @DisplayName("Проверка работы с специальными символами в fileHash")
    void specialCharacters_InFileHash_ShouldBeHandledCorrectly() {
        // Given
        String hashWithSpecialChars = "hash!@#$%^&*()_+-=[]{}|;:,.<>?";
        String status = "PROCESSING";

        // When
        UploadResponse uploadResponse = new UploadResponse(hashWithSpecialChars, status);

        // Then
        assertThat(uploadResponse.getFileHash()).isEqualTo(hashWithSpecialChars);
        assertThat(uploadResponse.getStatus()).isEqualTo(status);
    }

    @Test
    @DisplayName("Проверка работы с пробелами в статусе")
    void spaces_InStatus_ShouldBeHandledCorrectly() {
        // Given
        String fileHash = "hash123";
        String statusWithSpaces = "IN PROGRESS";

        // When
        UploadResponse uploadResponse = new UploadResponse(fileHash, statusWithSpaces);

        // Then
        assertThat(uploadResponse.getStatus()).isEqualTo("IN PROGRESS");
        assertThat(uploadResponse.getFileHash()).isEqualTo(fileHash);
    }
}
