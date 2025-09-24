package itmo.programming.service;

import itmo.programming.common.StatusEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Сервис для отправки событий в Kafka.
 */
@Slf4j
@Service
public class KafkaService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.kafka.topic.status:status-topic}")
    private String statusTopic;

    /**
     * Конструктор сервиса.
     *
     * @param kafkaTemplate шаблон Kafka для отправки сообщений
     */
    public KafkaService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }


    /**
     * Отправка события статуса в Kafka.
     *
     * @param event событие статуса
     */
    public void sendStatusEvent(StatusEvent event) {
        try {
            kafkaTemplate.send(statusTopic, event.getFileHash(), event);
            log.info("Sent status event: " + event.getFileHash() + " - " + event.getStatus());
        } catch (Exception e) {
            log.error("Failed to send status event: {}", e.getMessage(), e);
            throw new RuntimeException("Kafka communication error", e);
        }
    }
}