package service;

import itmo.programming.common.Status;
import itmo.programming.common.StatusEvent;
import itmo.programming.service.KafkaStatusConsumer;
import itmo.programming.service.StatusProcessingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class KafkaStatusConsumerTest {

    @Mock
    private StatusProcessingService statusService;

    @InjectMocks
    private KafkaStatusConsumer consumer;

    @Test
    @DisplayName("Consumer вызывает сервис обработки статуса при получении события")
    void shouldCallStatusService() {
        // Given
        StatusEvent event = new StatusEvent("fileHash123", Status.RECEIVED, "test.xlsx");

        // When
        consumer.consumeStatusEvent(event);

        // Then
        verify(statusService, times(1))
                .processStatusUpdate(eq(Status.RECEIVED), eq("fileHash123"), eq("test.xlsx"), isNull());
    }

    @Test
    @DisplayName("Consumer ловит исключения и логирует их")
    void shouldHandleExceptionGracefully() {
        // Given
        StatusEvent event = new StatusEvent("fileHash123", Status.RECEIVED, "test.xlsx");
        doThrow(new RuntimeException("Test exception"))
                .when(statusService)
                .processStatusUpdate(any(), any(), any(), any());

        // When
        consumer.consumeStatusEvent(event);

        // Then
        assertDoesNotThrow(() -> consumer.consumeStatusEvent(event));
        verify(statusService, atLeastOnce()).processStatusUpdate(any(), any(), any(), any());

    }
}
