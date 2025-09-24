package service;

import itmo.programming.common.FileEvent;
import itmo.programming.common.Status;
import itmo.programming.common.StatusEvent;
import itmo.programming.service.KafkaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaServiceTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    private KafkaService kafkaService;

    @BeforeEach
    void setUp() {
        kafkaService = new KafkaService(kafkaTemplate);
        ReflectionTestUtils.setField(kafkaService, "uploadTopic", "upload-topic");
        ReflectionTestUtils.setField(kafkaService, "statusTopic", "status-topic");
    }

    @Test
    @DisplayName("Должен бросать исключение при ошибке Kafka")
    void shouldThrowExceptionWhenKafkaFails() {
        // Given
        FileEvent event = new FileEvent("hash123", "test.xlsx", new byte[]{1, 2, 3});

        when(kafkaTemplate.send(eq("upload-topic"), eq("hash123"), any(FileEvent.class)))
                .thenThrow(new RuntimeException("Kafka error"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> kafkaService.sendFileToProcessing(event));

        assertEquals("Kafka communication error", exception.getMessage());
        assertNotNull(exception.getCause());
    }


    @Test
    @DisplayName("Должен обрабатывать исключение при отправке статуса")
    void shouldHandleExceptionWhenSendingStatus() {
        // Given
        StatusEvent event = new StatusEvent("hash123", Status.RECEIVED, "test.xlsx");

        when(kafkaTemplate.send(anyString(), anyString(), any(StatusEvent.class)))
                .thenThrow(new RuntimeException("Kafka status error"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> kafkaService.sendStatusEvent(event));

        assertEquals("Kafka communication error", exception.getMessage());
        assertNotNull(exception.getCause());
    }


}