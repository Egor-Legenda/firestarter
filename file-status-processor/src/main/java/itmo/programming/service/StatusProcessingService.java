package itmo.programming.service;

import itmo.programming.common.Status;
import itmo.programming.model.FileStatus;
import itmo.programming.repository.FileStatusRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Сервис для обработки обновлений статусов файлов.
 */
@Slf4j
@Service
public class StatusProcessingService {

    private final FileStatusRepository repository;

    /**
     * Конструктор сервиса.
     *
     * @param repository репозиторий для работы с FileStatus
     */
    public StatusProcessingService(FileStatusRepository repository) {
        this.repository = repository;
    }

    /**
     * Обработка обновления статуса файла.
     *
     * @param status       новый статус файла
     * @param fileHash     хеш файла
     * @param fileName     имя файла
     * @param errorMessage сообщение об ошибке, если есть
     */
    @Transactional
    public void processStatusUpdate(Status status, String fileHash, String fileName, String errorMessage) {
        FileStatus fileStatus = repository.findByFileHash(fileHash)
                .orElse(new FileStatus(fileHash, status, fileName));
        fileStatus.setStatus(status);

        if (errorMessage != null) {
            fileStatus.setErrorMessage(errorMessage);
        }
        repository.save(fileStatus);
        log.info("Updated status for file " + fileHash + " to " + status);
    }
}
