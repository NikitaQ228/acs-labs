package com.example.library.controller.api;

import com.example.library.dto.BookForm;
import com.example.library.dto.BookResponse;
import com.example.library.service.BookService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;

/**
 * Предоставляет операции REST API над книгами каталога.
 */
@RestController
@RequestMapping(path = "/api/books", produces = {APPLICATION_JSON_VALUE, APPLICATION_XML_VALUE})
@RequiredArgsConstructor
@Tag(name = "Книги")
public class BookRestController {

    private static final String JSON_EXAMPLE = """
            {"title":"Новая книга","isbn":"9780000000064","publicationYear":2026,"authorIds":[1,2]}
            """;
    private static final String XML_EXAMPLE = """
            <book>
                <title>Новая книга</title>
                <isbn>9780000000064</isbn>
                <publicationYear>2026</publicationYear>
                <authorIds><authorId>1</authorId><authorId>2</authorId></authorIds>
            </book>
            """;

    private final BookService bookService;

    /**
     * Возвращает каталог книг с идентификаторами их авторов.
     *
     * @return список книг
     */
    @GetMapping
    public BookResponse.Books list() {
        return new BookResponse.Books(bookService.findAll(null).stream().map(BookResponse::from).toList());
    }

    /**
     * Возвращает книгу по идентификатору.
     *
     * @param id идентификатор книги
     * @return книга или HTTP 404, если запись отсутствует
     */
    @GetMapping("/{id}")
    public BookResponse get(@PathVariable Long id) {
        return BookResponse.from(bookService.get(id));
    }

    /**
     * Создаёт книгу и связывает её с указанными авторами.
     *
     * @param form данные книги и идентификаторы авторов
     * @return HTTP 201, созданная книга и её адрес в заголовке Location
     */
    @PostMapping(consumes = {APPLICATION_JSON_VALUE, APPLICATION_XML_VALUE})
    @ApiResponse(responseCode = "201", description = "Книга создана", useReturnTypeSchema = true)
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = {
            @Content(mediaType = APPLICATION_JSON_VALUE, examples = @ExampleObject(value = JSON_EXAMPLE)),
            @Content(mediaType = APPLICATION_XML_VALUE, examples = @ExampleObject(value = XML_EXAMPLE))
    })
    public ResponseEntity<BookResponse> create(@Valid @RequestBody BookForm form) {
        BookResponse book = BookResponse.from(bookService.save(null, form));
        URI location = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{id}")
                .buildAndExpand(book.id()).toUri();
        return ResponseEntity.created(location).body(book);
    }

    /**
     * Обновляет данные книги и заменяет список её авторов.
     *
     * @param id идентификатор книги
     * @param form новые данные книги и идентификаторы авторов
     * @return обновлённая книга или HTTP 404, если запись отсутствует
     */
    @PutMapping(path = "/{id}", consumes = {APPLICATION_JSON_VALUE, APPLICATION_XML_VALUE})
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = {
            @Content(mediaType = APPLICATION_JSON_VALUE, examples = @ExampleObject(value = JSON_EXAMPLE)),
            @Content(mediaType = APPLICATION_XML_VALUE, examples = @ExampleObject(value = XML_EXAMPLE))
    })
    public BookResponse update(@PathVariable Long id, @Valid @RequestBody BookForm form) {
        return BookResponse.from(bookService.save(id, form));
    }

    /**
     * Удаляет книгу, сохраняя записи её авторов.
     *
     * @param id идентификатор удаляемой книги
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        bookService.delete(id);
    }
}
