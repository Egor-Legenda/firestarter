package itmo.programming.service;


import itmo.programming.common.StatusEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Сервис для потребления событий статусов из Kafka.
 */
@Slf4j
@Service
public class KafkaStatusConsumer {

    private final StatusProcessingService statusService;

    /**
     * Конструктор сервиса.
     *
     * @param statusService сервис для обработки статусов
     */
    public KafkaStatusConsumer(StatusProcessingService statusService) {
        this.statusService = statusService;
    }

    /**
     * Метод для прослушивания топика со статусами файлов.
     *
     * @param event событие статуса файла
     */
    @KafkaListener(
            topics = "${spring.kafka.topic.status:status-topic}",
            groupId = "${spring.kafka.consumer.group-id:file-status-processor-group}"
    )
    public void consumeStatusEvent(StatusEvent event) {
        try {
            log.info("Received status event: " + event.getFileHash() + " - " + event.getStatus());

            statusService.processStatusUpdate(
                    event.getStatus(),
                    event.getFileHash(),
                    event.getFileName(),
                    event.getErrorMessage()
            );

        } catch (Exception e) {
            log.error("Error processing status event: {}", e.getMessage(), e);
        }
    }
}
