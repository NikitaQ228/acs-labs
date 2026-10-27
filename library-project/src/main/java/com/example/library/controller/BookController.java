package com.example.library.controller;

import com.example.library.dto.BookForm;
import com.example.library.service.AuthorService;
import com.example.library.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * MVC-контроллер списка книг, форм редактирования и операций сохранения и удаления.
 */
@Controller
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final AuthorService authorService;

    /**
     * Направляет посетителя главной страницы к каталогу книг.
     *
     * @return перенаправление на список книг
     */
    @GetMapping("/")
    public String home() {
        return "redirect:/books";
    }

    /**
     * Показывает каталог с возможностью поиска.
     *
     * @param q запрос по названию, ISBN или автору; по умолчанию пустой
     * @param model данные страницы
     * @return имя шаблона списка книг
     */
    @GetMapping("/books")
    public String list(@RequestParam(defaultValue = "") String q, Model model) {
        model.addAttribute("books", bookService.findAll(q));
        model.addAttribute("q", q);
        return "books/list";
    }

    /**
     * Открывает пустую форму добавления книги.
     *
     * @param model данные страницы
     * @return имя шаблона формы книги
     */
    @GetMapping("/books/new")
    public String create(Model model) {
        model.addAttribute("bookForm", new BookForm());
        return form(null, model);
    }

    /**
     * Открывает форму с данными существующей книги и выбранными авторами.
     *
     * @param id идентификатор книги
     * @param model данные страницы
     * @return имя шаблона формы книги
     */
    @GetMapping("/books/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("bookForm", BookForm.from(bookService.get(id)));
        return form(id, model);
    }

    /**
     * Проверяет форму и сохраняет книгу. При ошибках повторно показывает введённые данные.
     *
     * @param id идентификатор книги или {@code null} при добавлении
     * @param bookForm данные формы
     * @param result ошибки привязки и проверки полей
     * @param model данные страницы при повторном показе формы
     * @param redirect уведомление после успешного сохранения
     * @return шаблон формы при ошибках или перенаправление в каталог
     */
    @PostMapping({"/books", "/books/{id}"})
    public String save(@PathVariable(required = false) Long id,
                       @Valid @ModelAttribute("bookForm") BookForm bookForm,
                       BindingResult result, Model model, RedirectAttributes redirect) {
        if (!result.hasFieldErrors("isbn") && bookService.isIsbnTaken(bookForm.getIsbn(), id)) {
            result.rejectValue("isbn", "duplicate", "Книга с таким ISBN уже есть в каталоге");
        }
        if (result.hasErrors()) {
            return form(id, model);
        }
        try {
            bookService.save(id, bookForm);
        } catch (IllegalArgumentException exception) {
            result.rejectValue("authorIds", "missing", exception.getMessage());
            return form(id, model);
        } catch (DataIntegrityViolationException exception) {
            result.rejectValue("isbn", "duplicate", "Книга с таким ISBN уже есть в каталоге");
            return form(id, model);
        }
        redirect.addFlashAttribute("success", id == null ? "Книга добавлена в коллекцию" : "Книга обновлена");
        return "redirect:/books";
    }

    /**
     * Удаляет книгу и возвращает пользователя в каталог.
     *
     * @param id идентификатор удаляемой книги
     * @param redirect уведомление об удалении
     * @return перенаправление на список книг
     */
    @PostMapping("/books/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        bookService.delete(id);
        redirect.addFlashAttribute("success", "Книга удалена из коллекции");
        return "redirect:/books";
    }

    /**
     * Добавляет в модель идентификатор книги и список доступных авторов.
     *
     * @param id идентификатор редактируемой книги или {@code null} для новой
     * @param model данные формы
     * @return имя шаблона формы книги
     */
    private String form(Long id, Model model) {
        model.addAttribute("bookId", id);
        model.addAttribute("authors", authorService.findAll(""));
        return "books/form";
    }
}
