package ru.ivan.ver.singlemessageconsumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.ivan.ver.common.TopicList;
import ru.ivan.ver.common.dto.MessageDto;

import java.time.Duration;
import java.util.Collections;

import static ru.ivan.ver.common.TopicList.*;

@SpringBootApplication
@RequiredArgsConstructor
@Slf4j
public class SingleMessageConsumerApplication implements CommandLineRunner {

    final KafkaConsumer<String, MessageDto> consumer;

    public static void main(String[] args) {
        SpringApplication.run(SingleMessageConsumerApplication.class, args);
    }

    @Override
    public void run(String... args) {
        consumer.subscribe(Collections.singletonList(MAIN_TOPIC));
        while (true) {
            try {
                ConsumerRecords<String, MessageDto> records = consumer.poll(Duration.ofMillis(100));
                records.forEach(it -> log.info("Key: %s, value: %s, partition: %d".formatted(it.key(), it.value(), it.partition())));
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }
}
