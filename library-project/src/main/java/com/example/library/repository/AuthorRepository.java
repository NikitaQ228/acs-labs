package com.example.library.repository;

import com.example.library.model.Athor;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * Доступ к авторам через Spring Data JPA с загрузкой книг для отображения в MVC.
 */
public interface AuthorRepository extends JpaRepository<Athor, Long> {

    /**
     * Загружает всех авторов вместе с их книгами.
     *
     * @return авторы, упорядоченные по фамилии и имени
     */
    @EntityGraph(attributePaths = "books")
    @Query("select a from Athor a order by a.lastName, a.firstName")
    List<Athor> findAllWithBooks();

    /**
     * Загружает автора и его книги по идентификатору.
     *
     * @param id идентификатор автора
     * @return автор или пустой {@link Optional}, если запись отсутствует
     */
    @Override
    @EntityGraph(attributePaths = "books")
    Optional<Athor> findById(Long id);
}
