package com.onedoorway.project.repository;

import com.onedoorway.project.model.BowelNote;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BowelNoteRepository extends JpaRepository<BowelNote, Long> {
    List<BowelNote> findAllByClient_IdAndStartDateBetween(
            Long id, LocalDate start, LocalDate end, Pageable pageable);
}
