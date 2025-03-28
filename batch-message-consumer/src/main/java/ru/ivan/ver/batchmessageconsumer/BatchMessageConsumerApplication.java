package ru.ivan.ver.batchmessageconsumer;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.ivan.ver.common.dto.MessageDto;

import java.time.Duration;
import java.util.Collections;

import static ru.ivan.ver.common.TopicList.MAIN_TOPIC;

@SpringBootApplication
@RequiredArgsConstructor
@Slf4j
public class BatchMessageConsumerApplication implements CommandLineRunner {

    final KafkaConsumer<String, MessageDto> consumer;

    public static void main(String[] args) {
        SpringApplication.run(BatchMessageConsumerApplication.class, args);
    }

    @SneakyThrows
    @Override
    public void run(String... args) {
        consumer.subscribe(Collections.singletonList(MAIN_TOPIC));
        while (true) {
            try {
                ConsumerRecords<String, MessageDto> records = consumer.poll(Duration.ofMillis(100));
                if (records.count() < 10) continue;                     // Если размер пакета меньше 10, то итерацию пропускаем
                log.info("Batch size - %s".formatted(records.count())); // Вывод в консоль размер пакета
                records
                        .forEach(it -> log.info("Key: %s, value: %s, partition: %d"
                                .formatted(it.key(), it.value(), it.partition())));  // Вывод в консоль каждого сообщения
                consumer.commitSync();   // Коммит
                Thread.sleep(1000); // Задержка, для того, чтобы накопилось необходимое количество сообщений в пакете
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }
}
