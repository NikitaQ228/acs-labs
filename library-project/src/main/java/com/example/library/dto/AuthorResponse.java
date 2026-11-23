package com.example.library.dto;

import com.example.library.model.Athor;
import com.example.library.model.Book;
import com.fasterxml.jackson.annotation.JsonRootName;
import tools.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.time.LocalDate;
import java.util.List;

/**
 * Представление автора в REST API.
 *
 * @param id идентификатор автора
 * @param firstName имя
 * @param lastName фамилия
 * @param birthDate дата рождения, если известна
 * @param bookIds идентификаторы связанных книг
 */
@JsonRootName("author")
public record AuthorResponse(
        Long id,
        String firstName,
        String lastName,
        LocalDate birthDate,
        @JacksonXmlElementWrapper(localName = "bookIds")
        @JacksonXmlProperty(localName = "bookId")
        List<Long> bookIds
) {

    /**
     * Копирует данные автора с идентификаторами его книг.
     *
     * @param author автор с загруженными книгами
     * @return представление автора
     */
    public static AuthorResponse from(Athor author) {
        return new AuthorResponse(author.getId(), author.getFirstName(), author.getLastName(), author.getBirthDate(),
                author.getBooks().stream().map(Book::getId).toList());
    }

    /**
     * Список авторов в REST API.
     *
     * @param authors авторы каталога
     */
    @JsonRootName("authors")
    public record Authors(
            @JacksonXmlElementWrapper(useWrapping = false)
            @JacksonXmlProperty(localName = "author")
            List<AuthorResponse> authors
    ) {
    }
}
