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

import static ru.ivan.ver.common.TopicList.*;

@SpringBootApplication
@RequiredArgsConstructor
public class ProducerApplication implements CommandLineRunner {
    /**
     * Kafka Producer для отправки сообщений.
     * Инициализируется автоматически через Spring DI.
     * Использует:
     * - String для ключа сообщения
     * - MessageDto для значения сообщения
     */
    final KafkaProducer<String, MessageDto> producer;

    /**
     * Точка входа в приложение.
     *
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        SpringApplication.run(ProducerApplication.class, args);
    }

    /**
     * Основная логика приложения после старта Spring контекста.
     * <p>
     * Отправляет сообщения в Kafka топик в бесконечном цикле с интервалом 50 мс.
     * Каждое сообщение содержит:
     * <ul>
     *   <li>Ключ в формате "key-{прядковый номер}"</li>
     *   <li>Значение - объект MessageDto с:
     *     <ul>
     *       <li>Темой сообщения в - фиксированным текстом</li>
     *       <li>Фиксированным текстом "message"</li>
     *       <li>Текущей датой и временем</li>
     *     </ul>
     *   </li>
     * </ul>
     *
     * Гарантирует закрытие producer'а при завершении работы приложения.
     *
     * @param args аргументы командной строки (не используются)
     * @throws InterruptedException если поток был прерван во время sleep
     */
    @SneakyThrows
    @Override
    public void run(String... args) {
        int num = 1;
        try {
            while (true) {
                // Отправка сообщения в Kafka
                producer.send(new ProducerRecord<>(
                        MAIN_TOPIC,                 // Название топика
                        "key-%s".formatted(num),    // Ключ сообщения
                        new MessageDto(             // Тело сообщения
                                "sub",              // Тема сообщения
                                "message",          // Текст сообщения
                                LocalDateTime.now() // Временная метка
                        )
                ));

                num++;                               // Инкремент счетчика
                Thread.sleep(50);              // Пауза между сообщениями
            }
        } finally {
            producer.close();
        }
    }
}
