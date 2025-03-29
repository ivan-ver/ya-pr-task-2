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

/**
 * Spring Boot приложение для поточной обработки сообщений из Kafka.
 * <p>
 * Основные характеристики:
 * <ul>
 *   <li>Подписывается на указанный Kafka топик (MAIN_TOPIC)</li>
 *   <li>Обрабатывает сообщения по одному по мере их поступления</li>
 *   <li>Логирует детальную информацию о каждом сообщении</li>
 *   <li>Автоматически подтверждает получение сообщений (auto-commit)</li>
 * </ul>
 */
@SpringBootApplication
@RequiredArgsConstructor
@Slf4j
public class SingleMessageConsumerApplication implements CommandLineRunner {

    /**
     * Kafka Consumer для получения сообщений.
     * Настроен на работу с:
     * - ключами типа String
     * - значениями типа MessageDto
     * Автоматически внедряется через Spring DI
     */
    final KafkaConsumer<String, MessageDto> consumer;

    /**
     * Точка входа в приложение.
     *
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        SpringApplication.run(SingleMessageConsumerApplication.class, args);
    }

    /**
     * Основная логика работы потребителя сообщений.
     * <p>
     * Работает по следующему алгоритму:
     * 1. Подписывается на основной топик (MAIN_TOPIC)
     * 2. В бесконечном цикле:
     *    - Получает сообщения с таймаутом 100 мс
     *    - Для каждого полученного сообщения:
     *      * Логирует ключ, значение и номер партиции
     *    - Обрабатывает возможные ошибки без прерывания работы
     *
     * @param args аргументы командной строки (не используются)
     */
    @Override
    public void run(String... args) {
        // Подписка на основной топик
        consumer.subscribe(Collections.singletonList(MAIN_TOPIC));
        while (true) {
            try {
                // Получение сообщений с таймаутом 100 мс
                ConsumerRecords<String, MessageDto> records = consumer.poll(Duration.ofMillis(100));

                // Обработка каждого сообщения в полученной партии
                records.forEach(it -> log.info("Key: %s, value: %s, partition: %d".formatted(it.key(), it.value(), it.partition())));
            } catch (Exception e) {
                // Логирование ошибок без прерывания работы
                log.error(e.getMessage());
            }
        }
    }
}
