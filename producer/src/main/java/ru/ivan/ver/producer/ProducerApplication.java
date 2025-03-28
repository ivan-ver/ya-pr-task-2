package ru.ivan.ver.producer;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.ivan.ver.common.TopicList;
import ru.ivan.ver.common.dto.MessageDto;

import java.time.LocalDateTime;
import java.util.UUID;

@SpringBootApplication
@RequiredArgsConstructor
public class ProducerApplication implements CommandLineRunner {

    final KafkaProducer<String, MessageDto> producer;

    public static void main(String[] args) {
        SpringApplication.run(ProducerApplication.class, args);
    }

    @SneakyThrows
    @Override
    public void run(String... args) {
        int num = 1;
        try {
            while (true) {
                String uuid = UUID.randomUUID().toString();
                producer.send(new ProducerRecord<>(TopicList.MAIN_TOPIC,
                        "key-%s".formatted(num), new MessageDto("sub-%s".formatted(uuid),
                        "message", LocalDateTime.now())));
                num ++;
                Thread.sleep(50);
            }
        } finally {
            producer.close();
        }
    }
}
