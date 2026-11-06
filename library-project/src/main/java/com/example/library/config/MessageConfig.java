package com.example.library.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.StaticMessageSource;

import java.util.Properties;

/**
 * Настраивает сообщения об ошибках преобразования полей без отдельного файла ресурсов.
 */
@Configuration
public class MessageConfig {

    /**
     * Создаёт источник сообщений, общих для всех языков интерфейса.
     *
     * @return источник русских сообщений для ошибок привязки данных формы
     */
    @Bean
    public MessageSource messageSource() {
        Properties messages = new Properties();
        messages.setProperty("typeMismatch.bookForm.publicationYear",
                "Введите год целым числом от 1 до 9999");
        messages.setProperty("typeMismatch.bookForm.authorIds",
                "Выберите авторов из списка");
        messages.setProperty("typeMismatch.authorForm.birthDate",
                "Введите корректную дату рождения");

        StaticMessageSource source = new StaticMessageSource();
        source.setCommonMessages(messages);
        return source;
    }
}
