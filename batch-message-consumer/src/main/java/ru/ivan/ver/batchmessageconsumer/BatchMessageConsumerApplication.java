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

/**
 * Spring Boot приложение для пакетной обработки сообщений из Kafka.
 * <p>
 * Основные характеристики:
 * <ul>
 *   <li>Подписывается на указанный Kafka топик</li>
 *   <li>Обрабатывает сообщения пакетами (минимум 10 сообщений)</li>
 *   <li>Логирует информацию о каждом пакете</li>
 *   <li>Поддерживает синхронное подтверждение обработки (commit)</li>
 * </ul>
 */
@SpringBootApplication
@RequiredArgsConstructor
@Slf4j
public class BatchMessageConsumerApplication implements CommandLineRunner {
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
        SpringApplication.run(BatchMessageConsumerApplication.class, args);
    }

    /**
     * Основная логика работы потребителя сообщений.
     * <p>
     * Алгоритм работы:
     * <ol>
     *   <li>Подписывается на основной топик (MAIN_TOPIC)</li>
     *   <li>В бесконечном цикле:
     *     <ul>
     *       <li>Получает сообщения с таймаутом 100 мс</li>
     *       <li>Пропускает небольшие пакеты (<10 сообщений)</li>
     *       <li>Логирует размер пакета</li>
     *       <li>Логирует детали каждого сообщения</li>
     *       <li>Подтверждает обработку (commitSync)</li>
     *       <li>Делает паузу 1 секунду между обработкой пакетов</li>
     *     </ul>
     *   </li>
     * </ol>
     *
     * @param args аргументы командной строки (не используются)
     */
    @SneakyThrows
    @Override
    public void run(String... args) {
        // Подписка на основной топик
        consumer.subscribe(Collections.singletonList(MAIN_TOPIC));
        while (true) {
            try {
                // Получение сообщений с таймаутом 100 мс
                ConsumerRecords<String, MessageDto> records = consumer.poll(Duration.ofMillis(100));

                // Пропуск небольших пакетов
                if (records.count() < 10) continue;

                // Логирование информации о пакете
                log.info("Batch size - %s".formatted(records.count()));

                // Обработка каждого сообщения в пакете
                records
                        .forEach(it -> log.info("Key: %s, value: %s, partition: %d"
                                .formatted(it.key(), it.value(), it.partition())));

                // Подтверждение обработки сообщений
                consumer.commitSync();

                // Пауза между обработкой пакетов
                Thread.sleep(1000);
            } catch (Exception e) {
                // Логирование ошибок без прерывания работы
                log.error(e.getMessage());
            }
        }
    }
}
