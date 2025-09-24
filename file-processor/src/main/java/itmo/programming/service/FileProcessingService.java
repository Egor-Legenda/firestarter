package itmo.programming.service;

import itmo.programming.common.FileEvent;
import itmo.programming.common.Status;
import itmo.programming.common.StatusEvent;
import itmo.programming.exception.FileValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Сервис для обработки файлов, полученных из Kafka
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileProcessingService {

    private final ExcelValidationService excelValidationService;
    private final KafkaService kafkaService;

    /**
     * Обработка файла из Kafka
     *
     * @param event событие с файлом
     */
    public void processFile(FileEvent event) {
        String fileHash = event.getFileHash();
        String fileName = event.getFileName();

        log.info("Processing file: {} ({})", fileName, fileHash);

        try {
            // Отправляем статус "начата обработка"
            sendStatus(fileHash, fileName, Status.SECONDARY_VALIDATION_STARTED, null);

            // Вторичная валидация содержимого Excel
            excelValidationService.validateExcelContent(event.getFileContent(), fileName);

            // Если валидация успешна
            sendStatus(fileHash, fileName, Status.SECONDARY_VALIDATION_SUCCESS, null);
            log.info("File validation successful: {}", fileHash);

        } catch (FileValidationException e) {
            sendStatus(fileHash, fileName, Status.SECONDARY_VALIDATION_FAILED, e.getMessage());
            log.error("File validation failed: {} - {}", fileHash, e.getMessage());

        } catch (Exception e) {
            sendStatus(fileHash, fileName, Status.SECONDARY_VALIDATION_ERROR, e.getMessage());
            log.error("Error processing file: {} - {}", fileHash, e.getMessage());
        }
    }

    /**
     * Отправка статуса обработки файла в Kafka.
     *
     * @param fileHash fileHash
     * @param fileName fileName
     * @param status статус обработки
     * @param errorMessage сообщение об ошибке (если есть)
     */
    private void sendStatus(String fileHash, String fileName, Status status, String errorMessage) {
        StatusEvent event = new StatusEvent(fileHash, status, fileName);
        if (errorMessage != null) {
            event.setErrorMessage(errorMessage);
        }
        kafkaService.sendStatusEvent(event);
    }
}
