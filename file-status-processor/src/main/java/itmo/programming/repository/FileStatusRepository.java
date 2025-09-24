package itmo.programming.repository;

import itmo.programming.model.FileStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

/**
 * Репозиторий для работы с FileStatus в MongoDB.
 */
public interface FileStatusRepository extends MongoRepository<FileStatus, String> {

    /**
     * Поиск FileStatus по хешу файла.
     *
     * @param fileHash Хеш файла.
     * @return Optional с найденным FileStatus или пустой, если не найдено.
     */
    Optional<FileStatus> findByFileHash(String fileHash);

    /**
     * Проверка существования FileStatus по хешу файла.
     *
     * @param fileHash Хеш файла.
     * @return true, если FileStatus с таким хешом существует, иначе false.
     */
    boolean existsByFileHash(String fileHash);
}
