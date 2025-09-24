package itmo.programming.service;



import itmo.programming.common.FileEvent;
import itmo.programming.common.StatusEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Сервис для отправки событий в Kafka.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.kafka.topic.upload:upload-topic}")
    private String uploadTopic;

    @Value("${spring.kafka.topic.status:status-topic}")
    private String statusTopic;

    /**
     * Отправка файла в топик для обработки
     *
     * @param event событие файла
     */
    public void sendFileToProcessing(FileEvent event) {
        try {
            kafkaTemplate.send(uploadTopic, event.getFileHash(), event);
            log.info("File sent to Kafka for processing: {}", event.getFileHash());
        } catch (Exception e) {
            log.error("Failed to send file to Kafka: {}", e.getMessage());
            throw new RuntimeException("Kafka communication error", e);
        }
    }

    /**
     * Отправка статуса файла
     *
     * @param event событие статуса
     */
    public void sendStatusEvent(StatusEvent event) {
        try {
            kafkaTemplate.send(statusTopic, event.getFileHash(), event);
            log.info("Status event sent: {} - {}", event.getFileHash(), event.getStatus());
        } catch (Exception e) {
            log.error("Failed to send status event: {}", e.getMessage());
            throw new RuntimeException("Kafka communication error", e);
        }
    }
}