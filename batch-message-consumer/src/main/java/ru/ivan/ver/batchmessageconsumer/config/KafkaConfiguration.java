package ru.ivan.ver.batchmessageconsumer.config;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.ivan.ver.common.dto.MessageDto;

import java.util.Properties;

import static org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_OFFSET_RESET_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.FETCH_MIN_BYTES_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.MAX_POLL_RECORDS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG;

/**
 * Конфигурационный класс для настройки Kafka Consumer.
 * <p>
 * Содержит настройки для подключения к Kafka и обработки сообщений.
 * Все параметры загружаются из конфигурационного файла (application.properties/yaml).
 */
@Configuration
public class KafkaConfiguration {
    /** Адреса брокеров Kafka для подключения */
    @Value("${spring.kafka.consumer.bootstrap-servers}")
    private String bootstrapServer;

    /** Десериализатор для ключей сообщений */
    @Value("${spring.kafka.consumer.key-deserializer}")
    private String keyDeserializer;

    /** Десериализатор для значений сообщений */
    @Value("${spring.kafka.consumer.value-deserializer}")
    private String valueDeserializer;

    /** Идентификатор группы потребителей */
    @Value("${spring.kafka.consumer.groupId}")
    private String groupId;

    /** Флаг автоматического подтверждения обработки сообщений */
    @Value("${spring.kafka.consumer.enable-auto-commit}")
    private Boolean enableAutoCommit;

    /** Стратегия поведения при отсутствии начального смещения */
    @Value("${spring.kafka.consumer.auto-offset-reset}")
    private String autoOffsetReset;

    /** Максимальное количество записей, возвращаемых при одном опросе */
    @Value("${spring.kafka.consumer.max-poll-records}")
    private Integer maxPollRecords;

    /** Минимальный размер данных для получения за один запрос (в байтах) */
    @Value("${spring.kafka.consumer.fetch-min-size}")
    private Integer fetchMinBytes;

    /** Максимальное время ожидания данных при запросе (в мс) */
    @Value("${spring.kafka.consumer.fetch-max-wait}")
    private Integer fetchMaxWait;

    /** Доверенные пакеты для десериализации JSON */
    @Value("${spring.kafka.consumer.properties.spring.json.trusted.packages}")
    private String packageName;

    /**
     * Создает конфигурацию для Kafka Consumer.
     *
     * @return Properties с настройками для Kafka Consumer
     */
    @Bean
    public Properties producerConfigs() {
        Properties properties = new Properties();
        properties.put(BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        properties.put(GROUP_ID_CONFIG, groupId);
        properties.put(KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializer);
        properties.put(VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializer);
        properties.put(AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        properties.put(ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit);
        properties.put(MAX_POLL_RECORDS_CONFIG, maxPollRecords);
        properties.put(FETCH_MIN_BYTES_CONFIG, fetchMinBytes);
        properties.put(FETCH_MAX_WAIT_MS_CONFIG, fetchMaxWait);
        properties.put("spring.json.trusted.packages", packageName);
        return properties;
    }

    /**
     * Создает и возвращает экземпляр KafkaConsumer.
     *
     * @param producerConfigs настройки потребителя
     * @return KafkaConsumer для работы с сообщениями типа MessageDto
     */
    @Bean
    public KafkaConsumer<String, MessageDto> consumer(Properties producerConfigs) {
        return new KafkaConsumer<>(producerConfigs);
    }
}
