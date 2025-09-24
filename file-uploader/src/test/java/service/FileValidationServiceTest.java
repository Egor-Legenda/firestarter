package service;

import itmo.programming.exception.FileValidationException;
import itmo.programming.service.FileValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class FileValidationServiceTest {

    @InjectMocks
    private FileValidationService validationService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(validationService, "maxFileSize", "5MB");
        ReflectionTestUtils.setField(validationService, "allowedExtensions", new String[]{"xls", "xlsx"});
    }

    @Test
    @DisplayName("Обработка валидного файла")
    void shouldValidateValidExcelFile() {
        MultipartFile file = new MockMultipartFile(
                "test.xlsx", "test.xlsx", "application/vnd.ms-excel", "test data".getBytes()
        );

        assertDoesNotThrow(() -> validationService.validateFile(file));
    }

    @Test
    @DisplayName("Обработка файла большего чем ограничения")
    void shouldThrowExceptionForLargeFile() {
        byte[] largeContent = new byte[6 * 1024 * 1024];
        MultipartFile file = new MockMultipartFile(
                "large.xlsx", "large.xlsx", "application/vnd.ms-excel", largeContent
        );

        FileValidationException exception = assertThrows(FileValidationException.class,
                () -> validationService.validateFile(file));

        assertTrue(exception.getMessage().contains("File size exceeds limit"));
    }

    @Test
    @DisplayName("Обработка файла с другим разрешением")
    void shouldThrowExceptionForInvalidExtension() {
        MultipartFile file = new MockMultipartFile(
                "test.pdf", "test.pdf", "application/pdf", "test data".getBytes()
        );

        FileValidationException exception = assertThrows(FileValidationException.class,
                () -> validationService.validateFile(file));

        assertTrue(exception.getMessage().contains("Invalid file extension"));
    }
}