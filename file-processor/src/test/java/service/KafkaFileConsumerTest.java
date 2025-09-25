package service;

import itmo.programming.common.FileEvent;
import itmo.programming.service.FileProcessingService;
import itmo.programming.service.KafkaFileConsumer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaFileConsumerTest {

    @Mock
    private FileProcessingService fileProcessingService;

    @InjectMocks
    private KafkaFileConsumer consumer;

    @Test
    @DisplayName("Consumer должен вызывать FileProcessingService при получении события")
    void shouldCallFileProcessingService() {
        // Given
        FileEvent event = new FileEvent("fileHash123", "test.xlsx", new byte[]{1, 2, 3});

        // When
        consumer.consumeFileEvent(event);

        // Then
        verify(fileProcessingService, times(1)).processFile(event);
    }

    @Test
    @DisplayName("Consumer должен ловить исключения и не выбрасывать их наружу")
    void shouldHandleExceptionGracefully() {
        // Given
        FileEvent event = new FileEvent("fileHash123", "test.xlsx", new byte[]{1, 2, 3});
        doThrow(new RuntimeException("Test exception")).when(fileProcessingService).processFile(event);

        // When + Then
        assertDoesNotThrow(() -> consumer.consumeFileEvent(event));

        // And
        verify(fileProcessingService, atLeastOnce()).processFile(event);
    }
}

