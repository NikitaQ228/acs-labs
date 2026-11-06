package com.example.library.controller;

import com.example.library.dto.AuthorForm;
import com.example.library.service.AuthorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
 * MVC-контроллер списка авторов, форм редактирования и операций сохранения и удаления.
 */
@Controller
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;

    /**
     * Показывает список авторов с возможностью поиска.
     *
     * @param q запрос по имени или фамилии; по умолчанию пустой
     * @param model данные страницы
     * @return имя шаблона списка авторов
     */
    @GetMapping("/authors")
    public String list(@RequestParam(defaultValue = "") String q, Model model) {
        model.addAttribute("authors", authorService.findAll(q));
        model.addAttribute("q", q);
        return "authors/list";
    }

    /**
     * Открывает пустую форму добавления автора.
     *
     * @param model данные страницы
     * @return имя шаблона формы автора
     */
    @GetMapping("/authors/new")
    public String create(Model model) {
        model.addAttribute("authorForm", new AuthorForm());
        model.addAttribute("authorId", null);
        return "authors/form";
    }

    /**
     * Открывает форму с данными существующего автора.
     *
     * @param id идентификатор автора
     * @param model данные страницы
     * @return имя шаблона формы автора
     */
    @GetMapping("/authors/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("authorForm", AuthorForm.from(authorService.get(id)));
        model.addAttribute("authorId", id);
        return "authors/form";
    }

    /**
     * Сохраняет автора после проверки формы либо возвращает форму с ошибками.
     *
     * @param id идентификатор автора или {@code null} при добавлении
     * @param authorForm данные формы
     * @param result ошибки привязки и проверки полей
     * @param model данные страницы при повторном показе формы
     * @param redirect уведомление после успешного сохранения
     * @return шаблон формы при ошибках или перенаправление на список авторов
     */
    @PostMapping({"/authors", "/authors/{id}"})
    public String save(@PathVariable(required = false) Long id,
                       @Valid @ModelAttribute("authorForm") AuthorForm authorForm,
                       BindingResult result, Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("authorId", id);
            return "authors/form";
        }
        authorService.save(id, authorForm);
        redirect.addFlashAttribute("success", id == null ? "Автор добавлен в каталог" : "Данные автора обновлены");
        return "redirect:/authors";
    }

    /**
     * Удаляет автора, сохраняя его книги в каталоге.
     *
     * @param id идентификатор удаляемого автора
     * @param redirect уведомление об удалении
     * @return перенаправление на список авторов
     */
    @PostMapping("/authors/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        authorService.delete(id);
        redirect.addFlashAttribute("success", "Автор удалён. Его книги сохранены в коллекции.");
        return "redirect:/authors";
    }
}
