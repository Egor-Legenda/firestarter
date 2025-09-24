package itmo.programming.service;

import itmo.programming.common.FileEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Сервис для потребления событий файлов из Kafka.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaFileConsumer {

    private final FileProcessingService fileProcessingService;

    /**
     * Метод для прослушивания топика с файлами для обработки.
     *
     * @param event событие файла для обработки
     */
    @KafkaListener(
            topics = "${spring.kafka.topic.upload:upload-topic}",
            groupId = "${spring.kafka.consumer.group-id:file-processor-group}"
    )
    public void consumeFileEvent(FileEvent event) {
        try {
            log.info("Received file for processing: {}", event.getFileHash());
            fileProcessingService.processFile(event);

        } catch (Exception e) {
            log.error("Error processing Kafka message: {}", e.getMessage(), e);
        }
    }
}
