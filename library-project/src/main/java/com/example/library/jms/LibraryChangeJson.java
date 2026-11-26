package com.example.library.jms;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.core.StreamReadFeature;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.cfg.CoercionAction;
import tools.jackson.databind.cfg.CoercionInputShape;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.type.LogicalType;

import java.util.Optional;

/** Сериализует события каталога и проверяет сообщения перед обработкой. */
@Component
public class LibraryChangeJson {

    private static final Logger logger = LoggerFactory.getLogger(LibraryChangeJson.class);

    private final Validator validator;
    private final JsonMapper mapper;

    /**
     * Настраивает строгое чтение JSON для событий каталога.
     *
     * @param validator валидатор полей события и снимка
     */
    public LibraryChangeJson(Validator validator) {
        this.validator = validator;
        mapper = JsonMapper.builder()
                .enable(StreamReadFeature.STRICT_DUPLICATE_DETECTION)
                .enable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS,
                        DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .disable(DeserializationFeature.ACCEPT_FLOAT_AS_INT)
                .disable(MapperFeature.ALLOW_COERCION_OF_SCALARS)
                .withCoercionConfig(LogicalType.Textual, config -> config
                        .setCoercion(CoercionInputShape.Integer, CoercionAction.Fail)
                        .setCoercion(CoercionInputShape.Float, CoercionAction.Fail)
                        .setCoercion(CoercionInputShape.Boolean, CoercionAction.Fail))
                .withCoercionConfig(LogicalType.Enum, config -> config
                        .setCoercion(CoercionInputShape.Integer, CoercionAction.Fail))
                .build();
    }

    /**
     * @param change событие каталога
     * @return JSON события для тела JMS-сообщения
     */
    public String write(LibraryChange change) {
        return mapper.writeValueAsString(change);
    }

    /**
     * @param snapshot снимок книги или автора
     * @return JSON снимка с типом сущности для хранения в журнале
     */
    public String writeSnapshot(LibraryChange.Snapshot snapshot) {
        return mapper.writerFor(LibraryChange.Snapshot.class).writeValueAsString(snapshot);
    }

    /**
     * Проверяет JMS-тип, структуру JSON и поля события. Причина отклонения записывается в лог.
     *
     * @param message входящее JMS-сообщение
     * @return проверенное событие или пустой результат для некорректного сообщения
     * @throws JMSException если JMS-провайдер не смог прочитать сообщение
     */
    public Optional<LibraryChange> read(Message message) throws JMSException {
        if (!(message instanceof TextMessage textMessage)) {
            logger.warn("Ignoring library JMS message {}: expected TextMessage", message.getJMSMessageID());
            return Optional.empty();
        }

        String body = textMessage.getText();
        LibraryChange change;
        try {
            change = body == null ? null : mapper.readValue(body, LibraryChange.class);
        } catch (JacksonException exception) {
            logger.warn("Ignoring library JMS message {}: invalid change JSON", message.getJMSMessageID());
            return Optional.empty();
        }
        if (change == null || !validator.validate(change).isEmpty()) {
            logger.warn("Ignoring library JMS message {}: invalid change fields", message.getJMSMessageID());
            return Optional.empty();
        }
        return Optional.of(change);
    }
}
