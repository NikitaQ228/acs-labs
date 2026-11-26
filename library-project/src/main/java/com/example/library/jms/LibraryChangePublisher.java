package com.example.library.jms;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import tools.jackson.core.JacksonException;

/** Публикует события об изменениях после коммита транзакции каталога. */
@Component
@RequiredArgsConstructor
public class LibraryChangePublisher {

    /** Имя JMS topic для событий каталога. */
    public static final String TOPIC = "library.changes";
    private static final Logger logger = LoggerFactory.getLogger(LibraryChangePublisher.class);

    private final JmsTemplate jmsTemplate;
    private final LibraryChangeJson json;

    /**
     * Отправляет снимок как JSON в текстовом JMS-сообщении. Ошибка отправки записывается в лог
     * после того, как изменение уже сохранено в базе.
     *
     * @param change событие, сформированное в транзакции каталога
     */
    @TransactionalEventListener
    public void sendAfterCommit(LibraryChange change) {
        try {
            String body = json.write(change);
            jmsTemplate.send(TOPIC, session -> session.createTextMessage(body));
        } catch (JmsException | JacksonException exception) {
            logger.error("Could not publish library change {} after the database commit", change.eventId(), exception);
        }
    }
}
