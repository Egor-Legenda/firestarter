package itmo.programming.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.kafka")
@Data
public class KafkaProperties {
    private String bootstrapServers;
    private Producer producer = new Producer();
    private Consumer consumer = new Consumer();

    @Data
    public static class Producer {
        private String keySerializer;
        private String valueSerializer;
    }

    @Data
    public static class Consumer {
        private String groupId;
        private String keyDeserializer;
        private String valueDeserializer;
    }
}
