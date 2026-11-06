package com.example.library.service;

import com.example.library.dto.BookForm;
import com.example.library.model.Athor;
import com.example.library.model.Book;
import com.example.library.repository.AuthorRepository;
import com.example.library.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Управляет книгами, проверкой уникальности ISBN и связями с авторами.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    /**
     * Ищет книги по названию, ISBN или имени автора без учёта регистра.
     *
     * @param query поисковая строка; пустая строка или {@code null} возвращает все книги
     * @return книги с загруженными авторами, отсортированные по названию
     */
    public List<Book> findAll(String query) {
        String search = query == null ? "" : query.strip().toLowerCase(Locale.ROOT);
        return bookRepository.findAllWithAuthors().stream()
                .filter(book -> book.getTitle().toLowerCase(Locale.ROOT).contains(search)
                        || book.getIsbn().contains(search)
                        || book.getAuthors().stream().anyMatch(author ->
                        (author.getFirstName() + " " + author.getLastName())
                                .toLowerCase(Locale.ROOT).contains(search)
                        || (author.getLastName() + " " + author.getFirstName())
                                .toLowerCase(Locale.ROOT).contains(search)))
                .toList();
    }

    /**
     * Находит книгу и загружает её авторов.
     *
     * @param id идентификатор книги
     * @return найденная книга
     * @throws ResponseStatusException с HTTP-статусом 404, если книга не найдена
     */
    public Book get(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Книга не найдена"));
    }

    /**
     * Проверяет, занят ли ISBN другой книгой.
     *
     * @param isbn проверяемый ISBN
     * @param id идентификатор текущей книги или {@code null} при создании
     * @return {@code true}, если ISBN уже используется другой записью
     */
    public boolean isIsbnTaken(String isbn, Long id) {
        return bookRepository.findByIsbn(isbn)
                .filter(book -> !Objects.equals(book.getId(), id)).isPresent();
    }

    /**
     * Создаёт или обновляет книгу, заменяя состав авторов выбранным в форме.
     * Изменения книги и связей сохраняются в одной транзакции.
     *
     * @param id идентификатор книги или {@code null} для создания
     * @param form проверенные данные книги и идентификаторы её авторов
     * @throws IllegalArgumentException если хотя бы один выбранный автор не существует
     * @throws ResponseStatusException с HTTP-статусом 404, если изменяемой книги нет
     * @throws org.springframework.dao.DataIntegrityViolationException если нарушены ограничения БД
     */
    @Transactional
    public void save(Long id, BookForm form) {
        List<Athor> authors = authorRepository.findAllById(form.getAuthorIds());
        if (authors.size() != form.getAuthorIds().size()) {
            throw new IllegalArgumentException("Один из авторов уже удалён. Выберите авторов заново.");
        }
        Book book = id == null ? new Book() : get(id);
        book.setTitle(form.getTitle().strip());
        book.setIsbn(form.getIsbn().strip());
        book.setPublicationYear(form.getPublicationYear());
        for (Athor author : new ArrayList<>(book.getAuthors())) {
            book.removeAuthor(author);
        }
        authors.forEach(book::addAuthor);
        bookRepository.saveAndFlush(book);
    }

    /**
     * Удаляет книгу и её связи, сохраняя записи авторов.
     *
     * @param id идентификатор удаляемой книги
     * @throws ResponseStatusException с HTTP-статусом 404, если книга не найдена
     */
    @Transactional
    public void delete(Long id) {
        bookRepository.delete(get(id));
    }
}
