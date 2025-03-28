package ru.ivan.ver.singlemessageconsumer;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.ivan.ver.common.dto.MessageDto;

import java.util.Properties;

import static org.apache.kafka.clients.consumer.ConsumerConfig.*;

@Configuration
public class KafkaConfiguration {
    @Value("${spring.kafka.consumer.bootstrap-servers}")
    private String bootstrapServer;
    @Value("${spring.kafka.consumer.key-deserializer}")
    private String keyDeserializer;
    @Value("${spring.kafka.consumer.value-deserializer}")
    private String valueDeserializer;
    @Value("${spring.kafka.consumer.groupId}")
    private String groupId;
    @Value("${spring.kafka.consumer.enable-auto-commit}")
    private Boolean enableAutoCommit;
    @Value("${spring.kafka.consumer.auto-offset-reset}")
    private String autoOffsetReset;

    @Bean
    public Properties producerConfigs() {
        Properties properties = new Properties();
        properties.put(BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        properties.put(GROUP_ID_CONFIG, groupId);
        properties.put(KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializer);
        properties.put(VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializer);
        properties.put(AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        properties.put(ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit);
        properties.put(MAX_POLL_RECORDS_CONFIG, 1);
        properties.put("spring.json.trusted.packages", "*");
        return properties;
    }

    @Bean
    public KafkaConsumer<String, MessageDto> consumer(Properties producerConfigs) {
        return new KafkaConsumer<>(producerConfigs);
    }
}
