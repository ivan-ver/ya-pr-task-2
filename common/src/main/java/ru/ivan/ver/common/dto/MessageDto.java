package ru.ivan.ver.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.time.LocalDateTime;

/**
 * DTO (Data Transfer Object) для представления сообщения.
 * Содержит тему, текст сообщения и дату/время создания.
 * Используется для передачи данных через Kafka.
 *
 * @param subject Тема сообщения (заголовок)
 * @param message Текст сообщения
 * @param dateTime Дата и время создания сообщения в формате "yyyy-MM-dd HH:mm:ss"
 *
 * @see LocalDateTimeSerializer Сериализатор для преобразования LocalDateTime в строку
 * @see LocalDateTimeDeserializer Десериализатор для преобразования строки в LocalDateTime
 * @see JsonFormat Аннотация для определения формата сериализации даты/времени
 */
public record MessageDto(
        String subject,
        String message,
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
        LocalDateTime dateTime
) {
}
