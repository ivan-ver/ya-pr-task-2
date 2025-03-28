package ru.ivan.ver.producer;

import lombok.SneakyThrows;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.config.TopicConfig;
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


@Configuration
public class KafkaConfiguration {
    @Value("${spring.kafka.producer.bootstrap-servers}")
    private String bootstrapServer;
    @Value("${spring.kafka.producer.key-serializer}")
    private String keySerializer;
    @Value("${spring.kafka.producer.value-serializer}")
    private String valueSerializer;
    @Value("${spring.kafka.producer.acks}")
    private String acksConfig;
    @Value("${spring.kafka.producer.retries}")
    private Integer retries;

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

    @Bean
    public KafkaProducer<String, MessageDto> producer(Properties producerConfigs) {
        return new KafkaProducer<>(producerConfigs);
    }

    /**
     Топик с заданным количеством реплик и партиций создается (если такого нет в kafka) автоматически при запуске приложения
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
