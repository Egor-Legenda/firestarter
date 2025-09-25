package itmo.programming.controller;

import itmo.programming.common.Status;
import itmo.programming.response.ErrorResponse;
import itmo.programming.common.FileEvent;
import itmo.programming.common.StatusEvent;
import itmo.programming.response.UploadResponse;
import itmo.programming.exception.FileValidationException;
import itmo.programming.service.FileValidationService;
import itmo.programming.service.KafkaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Контроллер для загрузки файлов.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class FileUploadController {

    private final FileValidationService validationService;
    private final KafkaService kafkaService;

    /**
     * Обработка загрузки файла.
     *
     * @param file загружаемый файл
     * @return ResponseEntity с результатом загрузки
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        String fileHash = null;
        try {
            fileHash = generateFileHash(file);
        } catch (IOException e) {
            log.error("Failed to read file for hashing: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(new ErrorResponse("INTERNAL_ERROR", "Failed to process file"));
        } catch (NoSuchAlgorithmException e) {
            log.error("Hashing algorithm not found: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(new ErrorResponse("INTERNAL_ERROR", "Server configuration error"));
        }


        try {
            log.info("File upload started: {}", file.getOriginalFilename());

            // 1. Первичная валидация
            validationService.validateFile(file);

            // 2. Отправка статуса "файл принят"
            sendStatusEvent(fileHash, file.getOriginalFilename(), Status.RECEIVED, null);

            // 3. Отправка статуса "первичная валидация успешна"
            sendStatusEvent(fileHash, file.getOriginalFilename(), Status.PRIMARY_VALIDATION_SUCCESS, null);

            // 4. Отправка файла в Kafka для обработки
            sendToKafkaUpload(file, fileHash);

            log.info("File uploaded successfully: {}", fileHash);
            return ResponseEntity.ok(new UploadResponse(fileHash, "File accepted and queued for processing"));

        } catch (FileValidationException e) {
            log.error("Validation failed for file: {}", e.getMessage());

            sendStatusEvent(fileHash, file.getOriginalFilename(), Status.PRIMARY_VALIDATION_FAILED, e.getMessage());


            return ResponseEntity.internalServerError()
                    .body(new ErrorResponse("VALIDATION_ERROR", e.getMessage()));

        } catch (Exception e) {
            log.error("Internal error during file upload: {}", e.getMessage());

            sendStatusEvent(fileHash, file.getOriginalFilename(), Status.UPLOAD_ERROR, e.getMessage());

            return ResponseEntity.internalServerError()
                    .body(new ErrorResponse("INTERNAL_ERROR", "File processing failed"));
        }
    }

    /**
     * Генерация SHA-256 хеша файла
     */
    private String generateFileHash(MultipartFile file) throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] fileBytes = file.getBytes();
        byte[] hashBytes = digest.digest(fileBytes);
        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }

        return hexString.toString();
    }

    /**
     * Отправка файла в Kafka для дальнейшей обработки
     */
    private void sendToKafkaUpload(MultipartFile file, String fileHash) throws IOException {
        FileEvent event = new FileEvent(
                fileHash,
                file.getOriginalFilename(),
                file.getBytes()
        );

        kafkaService.sendFileToProcessing(event);
    }

    /**
     * Отправка события статуса в Kafka
     */
    private void sendStatusEvent(String fileHash, String fileName, Status status, String errorMessage) {
        StatusEvent event = new StatusEvent(fileHash, status, fileName);
        if (errorMessage != null) {
            event.setErrorMessage(errorMessage);
        }
        kafkaService.sendStatusEvent(event);
    }
}