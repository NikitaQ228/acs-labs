package com.example.library.dto;

import com.example.library.model.Athor;
import com.example.library.model.Book;
import com.fasterxml.jackson.annotation.JsonRootName;
import tools.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

/**
 * Представление книги в REST API.
 *
 * @param id идентификатор книги
 * @param title название
 * @param isbn ISBN из 13 цифр
 * @param publicationYear год издания
 * @param authorIds идентификаторы связанных авторов
 */
@JsonRootName("book")
public record BookResponse(
        Long id,
        String title,
        String isbn,
        Integer publicationYear,
        @JacksonXmlElementWrapper(localName = "authorIds")
        @JacksonXmlProperty(localName = "authorId")
        List<Long> authorIds
) {

    /**
     * Копирует данные книги с идентификаторами её авторов.
     *
     * @param book книга с загруженными авторами
     * @return представление книги
     */
    public static BookResponse from(Book book) {
        return new BookResponse(book.getId(), book.getTitle(), book.getIsbn(), book.getPublicationYear(),
                book.getAuthors().stream().map(Athor::getId).toList());
    }

    /**
     * Список книг в REST API.
     *
     * @param books книги каталога
     */
    @JsonRootName("books")
    public record Books(
            @JacksonXmlElementWrapper(useWrapping = false)
            @JacksonXmlProperty(localName = "book")
            List<BookResponse> books
    ) {
    }
}
