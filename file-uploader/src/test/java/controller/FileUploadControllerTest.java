package controller;

import itmo.programming.controller.FileUploadController;
import itmo.programming.exception.FileValidationException;
import itmo.programming.response.ErrorResponse;
import itmo.programming.response.UploadResponse;
import itmo.programming.service.FileValidationService;
import itmo.programming.service.KafkaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.verification.VerificationMode;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.apache.kafka.common.config.ConfigDef.Range.atLeast;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileUploadControllerTest {

    @Mock
    private FileValidationService validationService;

    @Mock
    private KafkaService kafkaService;

    @InjectMocks
    private FileUploadController controller;

    @Test
    @DisplayName("Успешная загрузка валидного файла")
    void shouldUploadValidFile() throws Exception {
        // Given
        MultipartFile file = new MockMultipartFile(
                "file", "test.xlsx", "application/vnd.ms-excel", "test content".getBytes()
        );
        doNothing().when(validationService).validateFile(file);
        doNothing().when(kafkaService).sendFileToProcessing(any());
        doNothing().when(kafkaService).sendStatusEvent(any());

        // When
        ResponseEntity<?> response = controller.uploadFile(file);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        assertTrue(response.getBody() instanceof UploadResponse);
        UploadResponse uploadResponse = (UploadResponse) response.getBody();
        assertNotNull(uploadResponse.getFileHash());
        assertEquals("File accepted and queued for processing", uploadResponse.getStatus());

        verify(kafkaService, times(1)).sendFileToProcessing(any());
        verify(kafkaService, atMost(3)).sendStatusEvent(any());
    }

    @Test
    @DisplayName("Ошибка валидации файла приводит к VALIDATION_ERROR")
    void shouldReturnValidationError() throws Exception {
        // Given
        MultipartFile file = new MockMultipartFile(
                "file", "test.pdf", "application/pdf", "invalid content".getBytes()
        );
        doThrow(new FileValidationException("Invalid file type"))
                .when(validationService).validateFile(file);

        // When
        ResponseEntity<?> response = controller.uploadFile(file);

        // Then
        assertNotNull(response);
        assertEquals(500, response.getStatusCodeValue());

        assertTrue(response.getBody() instanceof ErrorResponse);
        ErrorResponse error = (ErrorResponse) response.getBody();
        assertEquals("VALIDATION_ERROR", error.getErrorType());
        assertEquals("Invalid file type", error.getMessage());

        verify(kafkaService, atMost(3)).sendStatusEvent(any());
        verify(kafkaService, never()).sendFileToProcessing(any());
    }

    @Test
    @DisplayName("Внутренняя ошибка при отправке в Kafka приводит к INTERNAL_ERROR")
    void shouldReturnInternalErrorOnUnexpectedException() throws Exception {
        // Given
        MultipartFile file = new MockMultipartFile(
                "file", "test.xlsx", "application/vnd.ms-excel", "test content".getBytes()
        );
        doNothing().when(validationService).validateFile(file);
        doThrow(new RuntimeException("Kafka is down"))
                .when(kafkaService).sendFileToProcessing(any());

        // When
        ResponseEntity<?> response = controller.uploadFile(file);

        // Then
        assertNotNull(response);
        assertEquals(500, response.getStatusCodeValue());

        assertTrue(response.getBody() instanceof ErrorResponse);
        ErrorResponse error = (ErrorResponse) response.getBody();
        assertEquals("INTERNAL_ERROR", error.getErrorType());
        assertEquals("File processing failed", error.getMessage());
        verify(kafkaService, times(1)).sendFileToProcessing(any());
    }


    @Test
    @DisplayName("Пустой файл успешно обрабатывается, но с предупреждением")
    void shouldHandleEmptyFile() throws Exception {
        // Given
        MultipartFile file = new MockMultipartFile(
                "file", "empty.xlsx", "application/vnd.ms-excel", new byte[0]
        );
        doNothing().when(validationService).validateFile(file);
        doNothing().when(kafkaService).sendFileToProcessing(any());
        doNothing().when(kafkaService).sendStatusEvent(any());

        // When
        ResponseEntity<?> response = controller.uploadFile(file);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        assertTrue(response.getBody() instanceof UploadResponse);
        UploadResponse uploadResponse = (UploadResponse) response.getBody();
        assertNotNull(uploadResponse.getFileHash());
        assertEquals("File accepted and queued for processing", uploadResponse.getStatus());

        verify(kafkaService, times(1)).sendFileToProcessing(any());
        verify(kafkaService,  atMost(3)).sendStatusEvent(any());
    }
}
