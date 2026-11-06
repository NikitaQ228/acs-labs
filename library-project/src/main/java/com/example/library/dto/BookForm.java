package com.example.library.dto;

import com.example.library.model.Athor;
import com.example.library.model.Book;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Данные формы книги. Авторы передаются идентификаторами, а не JPA-сущностями.
 */
@Getter
@Setter
public class BookForm {

    /** Обязательное название длиной не более 255 символов. */
    @NotBlank(message = "Укажите название книги")
    @Size(max = 255, message = "Название должно быть не длиннее 255 символов")
    private String title;

    /** ISBN из 13 цифр без пробелов и дефисов. */
    @NotBlank(message = "Укажите ISBN")
    @Pattern(regexp = "[0-9]{13}", message = "ISBN должен содержать ровно 13 цифр")
    private String isbn;

    /** Обязательный год издания от 1 до 9999. */
    @NotNull(message = "Укажите год издания")
    @Min(value = 1, message = "Год должен быть от 1 до 9999")
    @Max(value = 9999, message = "Год должен быть от 1 до 9999")
    private Integer publicationYear;

    /** Непустой набор идентификаторов выбранных авторов. */
    @NotEmpty(message = "Выберите хотя бы одного автора")
    private Set<Long> authorIds = new LinkedHashSet<>();

    /**
     * Копирует книгу и идентификаторы её авторов в форму редактирования.
     *
     * @param book книга с доступной коллекцией авторов
     * @return заполненная форма без изменения сущности
     */
    public static BookForm from(Book book) {
        BookForm form = new BookForm();
        form.setTitle(book.getTitle());
        form.setIsbn(book.getIsbn());
        form.setPublicationYear(book.getPublicationYear());
        form.setAuthorIds(book.getAuthors().stream().map(Athor::getId)
                .collect(Collectors.toCollection(LinkedHashSet::new)));
        return form;
    }
}
