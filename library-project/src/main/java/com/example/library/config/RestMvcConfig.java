package com.example.library.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverters;
import org.springframework.http.converter.xml.JacksonXmlHttpMessageConverter;
import org.springframework.web.accept.ContentNegotiationStrategy;
import org.springframework.web.accept.HeaderContentNegotiationStrategy;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tools.jackson.core.JsonGenerator;
import tools.jackson.dataformat.xml.ser.ToXmlGenerator;

import java.util.List;

/**
 * Добавляет настройку выбора XML и ссылку на таблицу стилей в XML-ответах.
 */
@Configuration
public class RestMvcConfig implements WebMvcConfigurer {

    /**
     * Для REST API отдаёт XML по параметру {@code format=xml}, в остальных случаях учитывает Accept.
     *
     * @param configurer настройка согласования форматов Spring MVC
     */
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.strategies(List.of(request -> {
            HttpServletRequest servletRequest = request.getNativeRequest(HttpServletRequest.class);
            if (servletRequest != null
                    && servletRequest.getRequestURI().startsWith(servletRequest.getContextPath() + "/api/")
                    && "xml".equals(request.getParameter("format"))) {
                return List.of(MediaType.APPLICATION_XML);
            }
            return ContentNegotiationStrategy.MEDIA_TYPE_ALL_LIST;
        }, new HeaderContentNegotiationStrategy()));
    }

    /**
     * Регистрирует Jackson XML со ссылкой на XSLT перед корневым элементом ответа.
     *
     * @param builder настройка преобразователей тела запроса и ответа
     */
    @Override
    public void configureMessageConverters(HttpMessageConverters.ServerBuilder builder) {
        builder.withXmlConverter(new LibraryXmlConverter());
    }

    /** Записывает инструкцию XSLT после XML-декларации, если она включена. */
    private static class LibraryXmlConverter extends JacksonXmlHttpMessageConverter {

        @Override
        protected void writePrefix(JsonGenerator generator, Object body) {
            ToXmlGenerator xml = (ToXmlGenerator) generator;
            xml.initGenerator();
            xml.writeRaw("<?xml-stylesheet type=\"text/xsl\" href=\"/xsl/library.xsl\"?>\n");
        }
    }
}
