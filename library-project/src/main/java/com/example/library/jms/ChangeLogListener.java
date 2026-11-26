package com.example.library.jms;

import com.example.library.model.LibraryChangeLog;
import com.example.library.repository.LibraryChangeLogRepository;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

/** Сохраняет проверенные события каталога в журнале изменений. */
@Component
@RequiredArgsConstructor
public class ChangeLogListener {

    private final LibraryChangeJson json;
    private final LibraryChangeLogRepository repository;

    /**
     * Сохраняет снимок события, если этот UUID еще отсутствует в журнале.
     *
     * @param message сообщение из подписки журнала
     * @throws JMSException если JMS-провайдер не смог прочитать сообщение
     */
    @JmsListener(destination = LibraryChangePublisher.TOPIC, subscription = "library-change-log",
            containerFactory = "libraryTopicListenerFactory")
    public void record(Message message) throws JMSException {
        json.read(message).ifPresent(change -> {
            if (!repository.existsByEventId(change.eventId())) {
                repository.save(new LibraryChangeLog(change, json.writeSnapshot(change.data())));
            }
        });
    }
}
