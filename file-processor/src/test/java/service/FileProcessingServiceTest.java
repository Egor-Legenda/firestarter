package service;


import itmo.programming.common.FileEvent;
import itmo.programming.common.Status;
import itmo.programming.common.StatusEvent;
import itmo.programming.exception.FileValidationException;
import itmo.programming.service.ExcelValidationService;
import itmo.programming.service.FileProcessingService;
import itmo.programming.service.KafkaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Сервис обработки файлов")
class FileProcessingServiceTest {

    @Mock
    private ExcelValidationService excelValidationService;

    @Mock
    private KafkaService kafkaService;

    @InjectMocks
    private FileProcessingService fileProcessingService;

    @Test
    @DisplayName("Должен успешно обработать валидный Excel файл")
    void shouldProcessValidExcelFileSuccessfully() {
        // Given
        FileEvent fileEvent = new FileEvent("hash123", "test.xlsx", new byte[]{1, 2, 3});

        doNothing().when(excelValidationService).validateExcelContent(any(), anyString());

        // When
        fileProcessingService.processFile(fileEvent);

        // Then
        verify(excelValidationService).validateExcelContent(fileEvent.getFileContent(), "test.xlsx");

        var statusOrder = inOrder(kafkaService);
        statusOrder.verify(kafkaService).sendStatusEvent(argThat(event ->
                event.getStatus().equals(Status.SECONDARY_VALIDATION_STARTED)));
        statusOrder.verify(kafkaService).sendStatusEvent(argThat(event ->
                event.getStatus().equals(Status.SECONDARY_VALIDATION_SUCCESS)));
    }

    @Test
    @DisplayName("Должен отправить статус ошибки при невалидном Excel файле")
    void shouldSendFailedStatusWhenExcelValidationFails() {
        // Given
        FileEvent fileEvent = new FileEvent("hash123", "test.xlsx", new byte[]{1, 2, 3});
        String errorMessage = "Неверное содержимое Excel файла";

        doThrow(new FileValidationException(errorMessage))
                .when(excelValidationService).validateExcelContent(any(), anyString());

        // When
        fileProcessingService.processFile(fileEvent);

        // Then
        verify(kafkaService).sendStatusEvent(argThat(event ->
                event.getStatus().equals(Status.SECONDARY_VALIDATION_FAILED) &&
                        event.getErrorMessage().equals(errorMessage)));

        verify(kafkaService, never()).sendStatusEvent(argThat(event ->
                event.getStatus().equals(Status.SECONDARY_VALIDATION_SUCCESS)));
    }

    @Test
    @DisplayName("Должен обработать непредвиденную ошибку при валидации")
    void shouldHandleUnexpectedExceptionDuringValidation() {
        // Given
        FileEvent fileEvent = new FileEvent("hash123", "test.xlsx", new byte[]{1, 2, 3});
        String errorMessage = "Внутренняя ошибка сервера";

        doThrow(new RuntimeException(errorMessage))
                .when(excelValidationService).validateExcelContent(any(), anyString());

        // When
        fileProcessingService.processFile(fileEvent);

        // Then
        verify(kafkaService).sendStatusEvent(argThat(event ->
                event.getStatus().equals(Status.SECONDARY_VALIDATION_ERROR) &&
                        event.getErrorMessage().equals(errorMessage)));
    }

    @Test
    @DisplayName("Должен отправлять статус начала обработки перед валидацией")
    void shouldSendStartedStatusBeforeValidation() {
        // Given
        FileEvent fileEvent = new FileEvent("hash123", "test.xlsx", new byte[]{1, 2, 3});

        doNothing().when(excelValidationService).validateExcelContent(any(), anyString());

        // When
        fileProcessingService.processFile(fileEvent);

        // Then
        var inOrder = inOrder(kafkaService, excelValidationService);

        inOrder.verify(kafkaService).sendStatusEvent(argThat(event ->
                event.getStatus().equals(Status.SECONDARY_VALIDATION_STARTED)));
        inOrder.verify(excelValidationService).validateExcelContent(any(), anyString());
    }

    @Test
    @DisplayName("Должен включать сообщение об ошибке в статус при неудачной валидации")
    void shouldIncludeErrorMessageInStatusWhenValidationFails() {
        // Given
        FileEvent fileEvent = new FileEvent("hash123", "test.xlsx", new byte[]{1, 2, 3});
        String expectedErrorMessage = "Файл не соответствует требованиям";

        doThrow(new FileValidationException(expectedErrorMessage))
                .when(excelValidationService).validateExcelContent(any(), anyString());

        // When
        fileProcessingService.processFile(fileEvent);

        // Then
        verify(kafkaService).sendStatusEvent(argThat(event ->
                event.getStatus().equals(Status.SECONDARY_VALIDATION_FAILED) &&
                        event.getErrorMessage() != null &&
                        event.getErrorMessage().equals(expectedErrorMessage)));
    }

    @Test
    @DisplayName("Должен логировать успешную обработку файла")
    void shouldLogSuccessfulFileProcessing() {
        // Given
        FileEvent fileEvent = new FileEvent("hash123", "test.xlsx", new byte[]{1, 2, 3});

        doNothing().when(excelValidationService).validateExcelContent(any(), anyString());

        // When
        fileProcessingService.processFile(fileEvent);

        // Then
        verify(kafkaService, times(2)).sendStatusEvent(any(StatusEvent.class));
    }
}
