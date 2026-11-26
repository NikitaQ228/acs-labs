package com.example.library.jms;

import com.example.library.model.Athor;
import com.example.library.model.Book;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Событие об изменении каталога со снимком данных на момент операции.
 *
 * @param eventId уникальный идентификатор события для учета повторной доставки
 * @param occurredAt время формирования снимка
 * @param operation выполненная операция
 * @param entityId идентификатор книги или автора
 * @param data новые данные для создания и обновления или данные до удаления
 */
public record LibraryChange(
        @NotNull UUID eventId,
        @NotNull Instant occurredAt,
        @NotNull Operation operation,
        @NotNull @Positive Long entityId,
        @NotNull @Valid Snapshot data) {

    /** Операция над книгой или автором. */
    public enum Operation { CREATE, UPDATE, DELETE }

    /** Тип измененной записи каталога. */
    public enum EntityType { BOOK, AUTHOR }

    /** Снимок сущности. Тип снимка хранится в его поле JSON {@code entityType}. */
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "entityType")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = BookData.class, name = "BOOK"),
            @JsonSubTypes.Type(value = AuthorData.class, name = "AUTHOR")
    })
    public sealed interface Snapshot permits BookData, AuthorData { }

    /**
     * Поля книги и ее связи с авторами на момент операции.
     *
     * @param title название книги
     * @param isbn ISBN из 13 цифр
     * @param publicationYear год издания
     * @param authorIds идентификаторы связанных авторов
     */
    public record BookData(
            @NotBlank @Size(max = 255) String title,
            @NotNull @Pattern(regexp = "[0-9]{13}") String isbn,
            @NotNull @Min(1) @Max(9999) Integer publicationYear,
            @NotNull List<@NotNull @Positive Long> authorIds) implements Snapshot { }

    /**
     * Поля автора и его связи с книгами на момент операции.
     *
     * @param firstName имя автора
     * @param lastName фамилия автора
     * @param birthDate дата рождения или {@code null}
     * @param bookIds идентификаторы связанных книг
     */
    public record AuthorData(
            @NotBlank @Size(max = 100) String firstName,
            @NotBlank @Size(max = 100) String lastName,
            LocalDate birthDate,
            @NotNull List<@NotNull @Positive Long> bookIds) implements Snapshot { }

    /** @return тип сущности, соответствующий варианту снимка */
    public EntityType entityType() {
        return switch (data) {
            case BookData ignored -> EntityType.BOOK;
            case AuthorData ignored -> EntityType.AUTHOR;
        };
    }

    /**
     * Копирует поля книги и идентификаторы ее авторов внутри транзакции каталога.
     *
     * @param operation операция над книгой
     * @param book книга с присвоенным идентификатором и доступной коллекцией авторов
     * @return событие с новым UUID и снимком книги
     */
    public static LibraryChange forBook(Operation operation, Book book) {
        BookData snapshot = new BookData(book.getTitle(), book.getIsbn(), book.getPublicationYear(),
                book.getAuthors().stream().map(Athor::getId).sorted().toList());
        return new LibraryChange(UUID.randomUUID(), Instant.now(), operation, book.getId(), snapshot);
    }

    /**
     * Копирует поля автора и идентификаторы его книг внутри транзакции каталога.
     *
     * @param operation операция над автором
     * @param author автор с присвоенным идентификатором и доступной коллекцией книг
     * @return событие с новым UUID и снимком автора
     */
    public static LibraryChange forAuthor(Operation operation, Athor author) {
        AuthorData snapshot = new AuthorData(author.getFirstName(), author.getLastName(), author.getBirthDate(),
                author.getBooks().stream().map(Book::getId).sorted().toList());
        return new LibraryChange(UUID.randomUUID(), Instant.now(), operation, author.getId(), snapshot);
    }
}
