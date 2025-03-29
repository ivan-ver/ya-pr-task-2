package ru.ivan.ver.producer.config;

import lombok.SneakyThrows;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.ivan.ver.common.TopicList;
import ru.ivan.ver.common.dto.MessageDto;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import static org.apache.kafka.clients.producer.ProducerConfig.ACKS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.RETRIES_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG;

/**
 * Конфигурационный класс для настройки Kafka Producer и администрирования топиков.
 * <p>
 * Создает и настраивает:
 * <ul>
 *   <li>Конфигурацию для Kafka Producer</li>
 *   <li>Экземпляр KafkaProducer для отправки сообщений</li>
 *   <li>AdminClient для управления топиками (создание, проверка существования)</li>
 * </ul>
 *
 * Настройки загружаются из application.properties/application.yml через аннотации @Value.
 */
@Configuration
public class KafkaConfiguration {
    /** Адреса брокеров Kafka для подключения */
    @Value("${spring.kafka.producer.bootstrap-servers}")
    private String bootstrapServer;

    /** Сериализатор для ключей сообщений */
    @Value("${spring.kafka.producer.key-serializer}")
    private String keySerializer;

    /** Сериализатор для значений сообщений */
    @Value("${spring.kafka.producer.value-serializer}")
    private String valueSerializer;

    /** Уровень подтверждения получения сообщения (acks) */
    @Value("${spring.kafka.producer.acks}")
    private String acksConfig;

    /** Количество повторных попыток отправки сообщения при ошибках */
    @Value("${spring.kafka.producer.retries}")
    private Integer retries;

    /**
     * Создает конфигурацию для Kafka Producer.
     *
     * @return Properties с настройками для Kafka Producer
     */
    @Bean
    public Properties producerConfigs() {
        Properties properties = new Properties();
        properties.put(BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        properties.put(KEY_SERIALIZER_CLASS_CONFIG, keySerializer);
        properties.put(VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer);
        properties.put(ACKS_CONFIG, acksConfig);
        properties.put(RETRIES_CONFIG, retries);
        return properties;
    }

    /**
     * Создает и возвращает экземпляр KafkaProducer для отправки сообщений.
     *
     * @param producerConfigs настройки producer'а
     * @return KafkaProducer для работы с сообщениями типа MessageDto
     */
    @Bean
    public KafkaProducer<String, MessageDto> producer(Properties producerConfigs) {
        return new KafkaProducer<>(producerConfigs);
    }

    /**
     * Создает и настраивает AdminClient для управления топиками Kafka.
     * <p>
     * При инициализации:
     * <ol>
     *   <li>Получает список всех топиков из класса TopicList</li>
     *   <li>Проверяет существующие топики в Kafka</li>
     *   <li>Создает отсутствующие топики с параметрами:
     *     <ul>
     *       <li>Количество партиций: 3</li>
     *       <li>Фактор репликации: 2</li>
     *     </ul>
     *   </li>
     * </ol>
     *
     * @return AdminClient для административных операций с Kafka
     * @throws RuntimeException если произошла ошибка при доступе к полям TopicList
     */
    @SneakyThrows
    @Bean
    public AdminClient adminClient() {
        AdminClient adminClient = AdminClient.create(new HashMap<>() {{
            put(BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        }});
        List<String> topicListForCreate = Arrays.stream(TopicList.class.getFields()).map(field -> {
            try {
                return field.get(field.getName());
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }).map(String::valueOf).toList();
        Set<String> currentTopicList = adminClient.listTopics().names().get();
        List<String> newTopicList = topicListForCreate.stream().filter(topic -> !currentTopicList.contains(topic)).toList();
        if (!newTopicList.isEmpty()) {
            adminClient.createTopics(newTopicList.stream()
                    .map(topicName -> new NewTopic(topicName, 3, (short) 2)).toList());
        }
        return adminClient;
    }

}
