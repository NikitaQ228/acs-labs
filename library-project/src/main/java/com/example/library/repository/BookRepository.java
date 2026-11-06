package com.example.library.repository;

import com.example.library.model.Book;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * Доступ к книгам через Spring Data JPA с загрузкой авторов для отображения в MVC.
 */
public interface BookRepository extends JpaRepository<Book, Long> {

    /**
     * Загружает все книги вместе с авторами.
     *
     * @return книги, упорядоченные по названию
     */
    @EntityGraph(attributePaths = "authors")
    @Query("select b from Book b order by b.title")
    List<Book> findAllWithAuthors();

    /**
     * Загружает книгу и её авторов по идентификатору.
     *
     * @param id идентификатор книги
     * @return книга или пустой {@link Optional}, если запись отсутствует
     */
    @Override
    @EntityGraph(attributePaths = "authors")
    Optional<Book> findById(Long id);

    /**
     * Ищет книгу по уникальному ISBN.
     *
     * @param isbn ISBN искомой книги
     * @return книга или пустой {@link Optional}, если ISBN отсутствует в каталоге
     */
    Optional<Book> findByIsbn(String isbn);
}
