package com.example.library.dto;

import com.example.library.model.Athor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * Данные формы создания или редактирования автора с правилами проверки полей.
 */
@Getter
@Setter
public class AuthorForm {

    /** Обязательное имя длиной не более 100 символов. */
    @NotBlank(message = "Укажите имя автора")
    @Size(max = 100, message = "Имя должно быть не длиннее 100 символов")
    private String firstName;

    /** Обязательная фамилия длиной не более 100 символов. */
    @NotBlank(message = "Укажите фамилию автора")
    @Size(max = 100, message = "Фамилия должна быть не длиннее 100 символов")
    private String lastName;

    /** Необязательная дата рождения, не позднее текущей даты. */
    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate birthDate;

    /**
     * Копирует сведения об авторе для заполнения формы редактирования.
     *
     * @param author существующий автор
     * @return заполненная форма без изменения сущности
     */
    public static AuthorForm from(Athor author) {
        AuthorForm form = new AuthorForm();
        form.setFirstName(author.getFirstName());
        form.setLastName(author.getLastName());
        form.setBirthDate(author.getBirthDate());
        return form;
    }
}
