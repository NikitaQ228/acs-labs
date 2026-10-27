package com.example.library.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Автор литературных произведений.
 * Связь с книгами является обратной стороной отношения многие ко многим;
 * изменения связей сохраняются через коллекцию авторов сущности {@link Book}.
 */
@Entity
@Table(name = "authors")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "books")
public class Athor {

    /** Идентификатор, который назначает база данных. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Имя автора. */
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    /** Фамилия автора. */
    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    /** Дата рождения; может отсутствовать. */
    @Column(name = "birth_date")
    private LocalDate birthDate;

    /** Книги автора, упорядоченные по названию при загрузке из базы данных. */
    @ManyToMany(mappedBy = "authors")
    @OrderBy("title ASC")
    private Set<Book> books = new LinkedHashSet<>();
}
