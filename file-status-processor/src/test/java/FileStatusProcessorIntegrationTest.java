
import itmo.programming.FileStatusProcessorApplication;
import itmo.programming.common.Status;
import itmo.programming.model.FileStatus;
import itmo.programming.repository.FileStatusRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest(classes = {FileStatusProcessorApplication.class})
class FileStatusProcessorIntegrationTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private FileStatusRepository repository;

    @Test
    @DisplayName("Проверка сохранения в базу данных")
    void shouldSaveAndRetrieveFileStatus() {
        FileStatus status = new FileStatus();
        status.setFileHash("test123");
        status.setStatus(Status.RECEIVED);
        status.setFileName("test.xlsx");

        FileStatus saved = repository.save(status);

        assertNotNull(saved.getId());
        assertEquals("test123", saved.getFileHash());
        assertEquals(Status.RECEIVED, saved.getStatus());
    }

    @Test
    @DisplayName("Проверка поиска по хешу")
    void shouldFindByFileHash() {
        FileStatus status = new FileStatus();
        status.setFileHash("unique123");
        status.setStatus(Status.PRIMARY_VALIDATION_SUCCESS);

        repository.save(status);

        var found = repository.findByFileHash("unique123");
        assertTrue(found.isPresent());
        assertEquals(Status.PRIMARY_VALIDATION_SUCCESS, found.get().getStatus());
    }
}