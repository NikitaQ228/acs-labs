package com.example.library.controller.api;

import com.example.library.dto.AuthorForm;
import com.example.library.dto.AuthorResponse;
import com.example.library.service.AuthorService;
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
 * Предоставляет операции REST API над авторами каталога.
 */
@RestController
@RequestMapping(path = "/api/authors", produces = {APPLICATION_JSON_VALUE, APPLICATION_XML_VALUE})
@RequiredArgsConstructor
@Tag(name = "Авторы")
public class AuthorRestController {

    private static final String JSON_EXAMPLE = """
            {"firstName":"Анна","lastName":"Иванова","birthDate":"1985-04-12"}
            """;
    private static final String XML_EXAMPLE = """
            <author>
                <firstName>Анна</firstName>
                <lastName>Иванова</lastName>
                <birthDate>1985-04-12</birthDate>
            </author>
            """;

    private final AuthorService authorService;

    /**
     * Возвращает авторов с идентификаторами их книг.
     *
     * @return список авторов
     */
    @GetMapping
    public AuthorResponse.Authors list() {
        return new AuthorResponse.Authors(authorService.findAll(null).stream().map(AuthorResponse::from).toList());
    }

    /**
     * Возвращает автора по идентификатору.
     *
     * @param id идентификатор автора
     * @return автор или HTTP 404, если запись отсутствует
     */
    @GetMapping("/{id}")
    public AuthorResponse get(@PathVariable Long id) {
        return AuthorResponse.from(authorService.get(id));
    }

    /**
     * Создаёт автора.
     *
     * @param form сведения об авторе
     * @return HTTP 201, созданный автор и его адрес в заголовке Location
     */
    @PostMapping(consumes = {APPLICATION_JSON_VALUE, APPLICATION_XML_VALUE})
    @ApiResponse(responseCode = "201", description = "Автор создан", useReturnTypeSchema = true)
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = {
            @Content(mediaType = APPLICATION_JSON_VALUE, examples = @ExampleObject(value = JSON_EXAMPLE)),
            @Content(mediaType = APPLICATION_XML_VALUE, examples = @ExampleObject(value = XML_EXAMPLE))
    })
    public ResponseEntity<AuthorResponse> create(@Valid @RequestBody AuthorForm form) {
        AuthorResponse author = AuthorResponse.from(authorService.save(null, form));
        URI location = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{id}")
                .buildAndExpand(author.id()).toUri();
        return ResponseEntity.created(location).body(author);
    }

    /**
     * Обновляет сведения об авторе, сохраняя его связи с книгами.
     *
     * @param id идентификатор автора
     * @param form новые сведения об авторе
     * @return обновлённый автор или HTTP 404, если запись отсутствует
     */
    @PutMapping(path = "/{id}", consumes = {APPLICATION_JSON_VALUE, APPLICATION_XML_VALUE})
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = {
            @Content(mediaType = APPLICATION_JSON_VALUE, examples = @ExampleObject(value = JSON_EXAMPLE)),
            @Content(mediaType = APPLICATION_XML_VALUE, examples = @ExampleObject(value = XML_EXAMPLE))
    })
    public AuthorResponse update(@PathVariable Long id, @Valid @RequestBody AuthorForm form) {
        return AuthorResponse.from(authorService.save(id, form));
    }

    /**
     * Удаляет автора и его связи с книгами, сохраняя сами книги.
     *
     * @param id идентификатор удаляемого автора
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        authorService.delete(id);
    }
}
