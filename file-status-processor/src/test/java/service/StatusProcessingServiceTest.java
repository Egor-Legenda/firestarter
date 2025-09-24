package service;

import itmo.programming.common.Status;
import itmo.programming.model.FileStatus;
import itmo.programming.repository.FileStatusRepository;
import itmo.programming.service.StatusProcessingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatusProcessingServiceTest {

    @Mock
    private FileStatusRepository fileStatusRepository;

    @InjectMocks
    private StatusProcessingService statusProcessingService;

    @Test
    @DisplayName("Должен создавать новую сущность при обновлении статуса для нового файла")
    void processStatusUpdate_NewFile_ShouldCreateNewEntity() {
        // Given
        String fileHash = "hash123";
        String fileName = "test.txt";
        Status status = Status.SECONDARY_VALIDATION_ERROR;

        when(fileStatusRepository.findByFileHash(fileHash)).thenReturn(Optional.empty());
        when(fileStatusRepository.save(any(FileStatus.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        statusProcessingService.processStatusUpdate(status, fileHash, fileName, null);

        // Then
        verify(fileStatusRepository).findByFileHash(fileHash);
        verify(fileStatusRepository).save(argThat(fileStatus ->
                fileHash.equals(fileStatus.getFileHash()) &&
                        status.equals(fileStatus.getStatus()) &&
                        fileName.equals(fileStatus.getFileName())
        ));
    }

    @Test
    @DisplayName("Должен обновлять статус при обновлении статуса для существующего файла")
    void processStatusUpdate_ExistingFile_ShouldUpdateStatus() {
        // Given
        String fileHash = "hash456";
        String fileName = "test.txt";
        FileStatus existingStatus = new FileStatus(fileHash, Status.SECONDARY_VALIDATION_SUCCESS, fileName);

        when(fileStatusRepository.findByFileHash(fileHash)).thenReturn(Optional.of(existingStatus));
        when(fileStatusRepository.save(any(FileStatus.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        statusProcessingService.processStatusUpdate(Status.SECONDARY_VALIDATION_SUCCESS, fileHash, fileName, null);

        // Then
        verify(fileStatusRepository).findByFileHash(fileHash);
        verify(fileStatusRepository).save(argThat(fileStatus ->
                Status.SECONDARY_VALIDATION_SUCCESS.equals(fileStatus.getStatus())
        ));
    }

    @Test
    @DisplayName("Должен устанавливать сообщение об ошибке при обновлении статуса с ошибкой")
    void processStatusUpdate_WithErrorMessage_ShouldSetErrorMessage() {
        // Given
        String fileHash = "hash789";
        String fileName = "test.txt";
        String errorMessage = "Processing failed";
        FileStatus existingStatus = new FileStatus(fileHash, Status.SECONDARY_VALIDATION_STARTED, fileName);

        when(fileStatusRepository.findByFileHash(fileHash)).thenReturn(Optional.of(existingStatus));
        when(fileStatusRepository.save(any(FileStatus.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        statusProcessingService.processStatusUpdate(Status.SECONDARY_VALIDATION_ERROR, fileHash, fileName, errorMessage);

        // Then
        verify(fileStatusRepository).save(argThat(fileStatus ->
                errorMessage.equals(fileStatus.getErrorMessage()) &&
                        Status.SECONDARY_VALIDATION_ERROR.equals(fileStatus.getStatus())
        ));
    }
}