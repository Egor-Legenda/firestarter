package itmo.programming.controller;


import itmo.programming.model.FileStatus;
import itmo.programming.repository.FileStatusRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Рест контроллер для обработки статусов.
 */
@RestController
@RequestMapping("/status")
public class StatusController {

    private final FileStatusRepository repository;

    /**
     * Конструктор контроллера.
     *
     * @param repository Репозиторий для работы с FileStatus.
     */
    public StatusController(FileStatusRepository repository) {
        this.repository = repository;
    }

    /**
     * Получение статуса файла по его хешу.
     *
     * @param fileHash Хеш файла.
     * @return ResponseEntity с FileStatus или 404, если не найдено.
     */
    @GetMapping("/{fileHash}")
    public ResponseEntity<?> getFileStatus(@PathVariable String fileHash) {
        return repository.findByFileHash(fileHash)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Получение всех статусов файлов.
     *
     * @return ResponseEntity с Iterable всех FileStatus.
     */
    @GetMapping
    public ResponseEntity<Iterable<FileStatus>> getAllStatuses() {
        return ResponseEntity.ok(repository.findAll());
    }
}