package com.example.library.controller.api;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Возвращает ошибки REST API в формате Problem Details.
 */
@RestControllerAdvice(basePackageClasses = BookRestController.class)
public class RestErrorAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    ProblemDetail invalidRequest(IllegalArgumentException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    ProblemDetail conflictingData() {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT,
                "Книга с таким ISBN уже существует или состав авторов изменился. Проверьте данные книги.");
    }
}
