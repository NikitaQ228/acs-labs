package com.example.library.jms;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/** Отправляет почтовые уведомления об удалении книг. */
@Component
public class BookDeletionMailListener {

    private final LibraryChangeJson json;
    private final JavaMailSender mailSender;
    private final String from;
    private final String[] recipients;

    /**
     * @param json компонент чтения и проверки событий
     * @param mailSender настроенный SMTP-отправитель
     * @param from адрес отправителя из конфигурации
     * @param recipients адреса получателей из конфигурации
     */
    public BookDeletionMailListener(LibraryChangeJson json, JavaMailSender mailSender,
                                    @Value("${library.mail.from}") String from,
                                    @Value("${library.mail.to}") String[] recipients) {
        this.json = json;
        this.mailSender = mailSender;
        this.from = from;
        this.recipients = recipients;
    }

    /**
     * Для проверенного события удаления книги отправляет письмо с ее названием, ISBN и ID.
     *
     * @param message сообщение из почтовой подписки
     * @throws JMSException если JMS-провайдер не смог прочитать сообщение
     */
    @JmsListener(destination = LibraryChangePublisher.TOPIC, subscription = "library-book-deletion-mail",
            containerFactory = "libraryTopicListenerFactory")
    public void notifyDeletion(Message message) throws JMSException {
        json.read(message).ifPresent(change -> {
            if (change.operation() == LibraryChange.Operation.DELETE && change.data() instanceof LibraryChange.BookData book) {
                SimpleMailMessage mail = new SimpleMailMessage();
                mail.setFrom(from);
                mail.setTo(recipients);
                mail.setSubject("Книга удалена из библиотеки");
                mail.setText("Название: %s%nISBN: %s%nID: %d".formatted(book.title(), book.isbn(), change.entityId()));
                mailSender.send(mail);
            }
        });
    }
}
