package com.example.library.repository;

import com.example.library.model.LibraryChangeLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/** Доступ к журналу изменений каталога через Spring Data JPA. */
public interface LibraryChangeLogRepository extends JpaRepository<LibraryChangeLog, Long> {

    /**
     * @param eventId UUID полученного события
     * @return {@code true}, если событие уже записано в журнал
     */
    boolean existsByEventId(UUID eventId);
}
