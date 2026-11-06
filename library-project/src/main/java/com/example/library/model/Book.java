package com.example.library.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Книга в библиотеке. Владеет связью с авторами через таблицу {@code book_authors}.
 * Удаление книги не удаляет записи её авторов.
 */
@Entity
@Table(name = "books")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "authors")
public class Book {

    /** Идентификатор, который назначает база данных. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Название книги. */
    @Column(nullable = false)
    private String title;

    /** Уникальный в каталоге ISBN из 13 цифр. */
    @Column(nullable = false, unique = true, length = 13)
    private String isbn;

    /** Год издания книги. */
    @Column(name = "publication_year", nullable = false)
    private Integer publicationYear;

    /** Авторы книги; при загрузке упорядочиваются по фамилии и имени. */
    @ManyToMany
    @OrderBy("lastName ASC, firstName ASC")
    @JoinTable(
            name = "book_authors",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    private Set<Athor> authors = new LinkedHashSet<>();

    /**
     * Добавляет автора и синхронизирует обе стороны связи в памяти.
     *
     * @param author автор, которого нужно связать с книгой; не {@code null}
     */
    public void addAuthor(Athor author) {
        authors.add(author);
        author.getBooks().add(this);
    }

    /**
     * Убирает связь с автором с обеих сторон, сохраняя саму запись автора.
     *
     * @param author автор, связь с которым нужно убрать; не {@code null}
     */
    public void removeAuthor(Athor author) {
        authors.remove(author);
        author.getBooks().remove(this);
    }
}
