package com.example.library.model;

import com.example.library.jms.LibraryChange;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

/** Запись журнала, которая хранит снимок независимо от существования сущности каталога. */
@Entity
@Table(name = "library_change_log")
public class LibraryChangeLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private UUID eventId;

    @Column(nullable = false)
    private Instant occurredAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private LibraryChange.Operation operation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private LibraryChange.EntityType entityType;

    @Column(nullable = false)
    private Long entityId;

    @Lob
    @Column(nullable = false)
    private String changeData;

    /** Конструктор для JPA. */
    protected LibraryChangeLog() { }

    /**
     * @param change событие, из которого копируются идентификаторы, время и операция
     * @param changeData снимок данных сущности в формате JSON
     */
    public LibraryChangeLog(LibraryChange change, String changeData) {
        eventId = change.eventId();
        occurredAt = change.occurredAt();
        operation = change.operation();
        entityType = change.entityType();
        entityId = change.entityId();
        this.changeData = changeData;
    }
}
