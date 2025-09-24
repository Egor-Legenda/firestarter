package response;


import itmo.programming.response.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.assertj.core.api.Assertions.*;

class ErrorResponseTest {

    @Test
    @DisplayName("Конструктор должен корректно инициализировать поля")
    void constructor_ShouldInitializeFieldsCorrectly() {
        // Given
        String errorType = "VALIDATION_ERROR";
        String message = "File validation failed";

        // When
        ErrorResponse errorResponse = new ErrorResponse(errorType, message);

        // Then
        assertThat(errorResponse.getErrorType()).isEqualTo(errorType);
        assertThat(errorResponse.getMessage()).isEqualTo(message);
    }

    @Test
    @DisplayName("Setter'ы должны корректно устанавливать значения")
    void setters_ShouldSetValuesCorrectly() {
        // Given
        ErrorResponse errorResponse = new ErrorResponse("OLD_ERROR", "Old message");
        String newErrorType = "NEW_ERROR";
        String newMessage = "New error message";

        // When
        errorResponse.setErrorType(newErrorType);
        errorResponse.setMessage(newMessage);

        // Then
        assertThat(errorResponse.getErrorType()).isEqualTo(newErrorType);
        assertThat(errorResponse.getMessage()).isEqualTo(newMessage);
    }

    @Test
    @DisplayName("Два объекта с одинаковыми полями должны быть равны")
    void equals_ShouldReturnTrueForSameFields() {
        // Given
        ErrorResponse response1 = new ErrorResponse("ERROR_TYPE", "Error message");
        ErrorResponse response2 = new ErrorResponse("ERROR_TYPE", "Error message");

        // Then
        assertThat(response1).isEqualTo(response2);
        assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
    }

    @Test
    @DisplayName("Два объекта с разными полями не должны быть равны")
    void equals_ShouldReturnFalseForDifferentFields() {
        // Given
        ErrorResponse response1 = new ErrorResponse("ERROR_1", "Message 1");
        ErrorResponse response2 = new ErrorResponse("ERROR_2", "Message 2");

        // Then
        assertThat(response1).isNotEqualTo(response2);
        assertThat(response1.hashCode()).isNotEqualTo(response2.hashCode());
    }

    @Test
    @DisplayName("Объект не должен быть равен null")
    void equals_ShouldReturnFalseForNull() {
        // Given
        ErrorResponse errorResponse = new ErrorResponse("ERROR", "Message");

        // Then
        assertThat(errorResponse).isNotEqualTo(null);
    }

    @Test
    @DisplayName("Объект не должен быть равен объекту другого класса")
    void equals_ShouldReturnFalseForDifferentClass() {
        // Given
        ErrorResponse errorResponse = new ErrorResponse("ERROR", "Message");
        String otherObject = "Not an ErrorResponse";

        // Then
        assertThat(errorResponse).isNotEqualTo(otherObject);
    }

    @Test
    @DisplayName("Метод toString должен возвращать непустую строку с информацией об объекте")
    void toString_ShouldReturnNonEmptyStringWithObjectInfo() {
        // Given
        ErrorResponse errorResponse = new ErrorResponse("VALIDATION_ERROR", "Invalid file format");

        // When
        String result = errorResponse.toString();

        // Then
        assertThat(result).isNotBlank();
        assertThat(result).contains("VALIDATION_ERROR", "Invalid file format");
        assertThat(result).contains("ErrorResponse");
    }

    @Test
    @DisplayName("Работа с null значениями в полях")
    void nullValues_ShouldBeHandledCorrectly() {
        // When
        ErrorResponse errorResponse = new ErrorResponse(null, null);

        // Then
        assertThat(errorResponse.getErrorType()).isNull();
        assertThat(errorResponse.getMessage()).isNull();
    }

    @Test
    @DisplayName("Setter'ы должны корректно работать с null значениями")
    void setters_ShouldHandleNullValuesCorrectly() {
        // Given
        ErrorResponse errorResponse = new ErrorResponse("ERROR", "Message");

        // When
        errorResponse.setErrorType(null);
        errorResponse.setMessage(null);

        // Then
        assertThat(errorResponse.getErrorType()).isNull();
        assertThat(errorResponse.getMessage()).isNull();
    }

    @Test
    @DisplayName("Проверка работы с пустыми строками")
    void emptyStrings_ShouldBeHandledCorrectly() {
        // Given
        String emptyErrorType = "";
        String emptyMessage = "";

        // When
        ErrorResponse errorResponse = new ErrorResponse(emptyErrorType, emptyMessage);

        // Then
        assertThat(errorResponse.getErrorType()).isEmpty();
        assertThat(errorResponse.getMessage()).isEmpty();
    }

    @Test
    @DisplayName("Проверка иммутабельности строковых полей при внешних изменениях")
    void stringFields_ShouldNotBeAffectedByExternalChanges() {
        // Given
        String originalErrorType = "ORIGINAL_ERROR";
        String originalMessage = "Original message";
        ErrorResponse errorResponse = new ErrorResponse(originalErrorType, originalMessage);

        // When
        originalErrorType = "MODIFIED_ERROR";
        originalMessage = "Modified message";

        // Then
        assertThat(errorResponse.getErrorType()).isEqualTo("ORIGINAL_ERROR");
        assertThat(errorResponse.getMessage()).isEqualTo("Original message");
    }

    @Test
    @DisplayName("Создание объекта с различными типами ошибок")
    void objectCreation_WithVariousErrorTypes() {
        // Given
        String[] errorTypes = {"VALIDATION_ERROR", "IO_ERROR", "RUNTIME_ERROR", "BUSINESS_ERROR"};

        for (String errorType : errorTypes) {
            // When
            ErrorResponse errorResponse = new ErrorResponse(errorType, "Test message");

            // Then
            assertThat(errorResponse.getErrorType()).isEqualTo(errorType);
            assertThat(errorResponse.getMessage()).isEqualTo("Test message");
        }
    }

    @Test
    @DisplayName("Проверка консистентности equals и hashCode")
    void equalsAndHashCode_ShouldBeConsistent() {
        // Given
        ErrorResponse response1 = new ErrorResponse("ERROR", "Message");
        ErrorResponse response2 = new ErrorResponse("ERROR", "Message");

        // When & Then
        assertThat(response1.equals(response2)).isTrue();
        assertThat(response1.hashCode() == response2.hashCode()).isTrue();

        response2.setMessage("New message");
        assertThat(response1.equals(response2)).isFalse();
        assertThat(response1.hashCode() == response2.hashCode()).isFalse();
    }

    @Test
    @DisplayName("Рефлексивность equals")
    void equals_ShouldBeReflexive() {
        // Given
        ErrorResponse errorResponse = new ErrorResponse("ERROR", "Message");

        // Then
        assertThat(errorResponse).isEqualTo(errorResponse);
    }

    @Test
    @DisplayName("Симметричность equals")
    void equals_ShouldBeSymmetric() {
        // Given
        ErrorResponse response1 = new ErrorResponse("ERROR", "Message");
        ErrorResponse response2 = new ErrorResponse("ERROR", "Message");

        // Then
        assertThat(response1.equals(response2)).isTrue();
        assertThat(response2.equals(response1)).isTrue();
    }

    @Test
    @DisplayName("Транзитивность equals")
    void equals_ShouldBeTransitive() {
        // Given
        ErrorResponse response1 = new ErrorResponse("ERROR", "Message");
        ErrorResponse response2 = new ErrorResponse("ERROR", "Message");
        ErrorResponse response3 = new ErrorResponse("ERROR", "Message");

        // Then
        assertThat(response1.equals(response2)).isTrue();
        assertThat(response2.equals(response3)).isTrue();
        assertThat(response1.equals(response3)).isTrue();
    }
}
