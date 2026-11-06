package com.example.library.service;

import com.example.library.dto.AuthorForm;
import com.example.library.model.Athor;
import com.example.library.model.Book;
import com.example.library.repository.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Управляет авторами и изменением их связей с книгами в транзакциях.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthorService {

    private final AuthorRepository authorRepository;

    /**
     * Ищет авторов по имени и фамилии без учёта регистра.
     *
     * @param query поисковая строка; пустая строка или {@code null} возвращает всех
     * @return авторы с загруженными книгами, отсортированные по фамилии и имени
     */
    public List<Athor> findAll(String query) {
        String search = query == null ? "" : query.strip().toLowerCase(Locale.ROOT);
        return authorRepository.findAllWithBooks().stream()
                .filter(author -> (author.getFirstName() + " " + author.getLastName())
                        .toLowerCase(Locale.ROOT).contains(search)
                        || (author.getLastName() + " " + author.getFirstName())
                        .toLowerCase(Locale.ROOT).contains(search))
                .toList();
    }

    /**
     * Находит автора и загружает его книги.
     *
     * @param id идентификатор автора
     * @return найденный автор
     * @throws ResponseStatusException с HTTP-статусом 404, если автор не найден
     */
    public Athor get(Long id) {
        return authorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Автор не найден"));
    }

    /**
     * Создаёт автора или обновляет его сведения, не меняя связей с книгами.
     *
     * @param id идентификатор автора или {@code null} для создания
     * @param form проверенные данные формы
     * @throws ResponseStatusException с HTTP-статусом 404, если изменяемого автора нет
     */
    @Transactional
    public void save(Long id, AuthorForm form) {
        Athor author = id == null ? new Athor() : get(id);
        author.setFirstName(form.getFirstName().strip());
        author.setLastName(form.getLastName().strip());
        author.setBirthDate(form.getBirthDate());
        authorRepository.save(author);
    }

    /**
     * Удаляет автора и его связи с книгами. Сами книги остаются в каталоге.
     *
     * @param id идентификатор удаляемого автора
     * @throws ResponseStatusException с HTTP-статусом 404, если автор не найден
     */
    @Transactional
    public void delete(Long id) {
        Athor author = get(id);
        for (Book book : new ArrayList<>(author.getBooks())) {
            book.removeAuthor(author);
        }
        authorRepository.delete(author);
    }
}
