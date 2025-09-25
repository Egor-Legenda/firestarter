package service;

import itmo.programming.common.Status;
import itmo.programming.common.StatusEvent;
import itmo.programming.service.KafkaService;
import org.apache.poi.EmptyFileException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.testcontainers.shaded.com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaServiceTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private KafkaService kafkaService;

    @Test
    @DisplayName("sendStatusEvent должен пробрасывать RuntimeException при ошибке Kafka")
    void shouldThrowRuntimeExceptionOnKafkaError() {
        // Given
        StatusEvent event = new StatusEvent("fileHash123", Status.RECEIVED, "test.xlsx");
        when(kafkaTemplate.send(any(), any(), any())).thenThrow(new RuntimeException("Kafka down"));

        // When + Then
        assertThrows(RuntimeException.class, () ->kafkaService.sendStatusEvent(event));
    }
}