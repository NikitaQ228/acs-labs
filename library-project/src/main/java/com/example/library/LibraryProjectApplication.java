package com.example.library;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Точка входа в Spring Boot-приложение для ведения каталога книг и авторов.
 */
@SpringBootApplication
public class LibraryProjectApplication {

    /**
     * Запускает приложение, включая веб-сервер и подключение к базе данных.
     *
     * @param args аргументы командной строки Spring Boot
     */
    public static void main(String[] args) {
        SpringApplication.run(LibraryProjectApplication.class, args);
    }

}
