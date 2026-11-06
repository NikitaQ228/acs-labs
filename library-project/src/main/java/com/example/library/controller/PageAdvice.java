package com.example.library.controller;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

/**
 * Общие настройки привязки данных HTML-форм к объектам контроллеров.
 */
@ControllerAdvice
public class PageAdvice {

    /**
     * Удаляет пробелы по краям строк и заменяет пустые строки на {@code null}.
     *
     * @param binder механизм привязки и проверки полей формы
     */
    @InitBinder
    public void trimStrings(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }
}
