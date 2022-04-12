package com.onedoorway.project.repository;

import com.onedoorway.project.model.CaseNote;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CaseNoteRepository extends JpaRepository<CaseNote, Long> {
    List<CaseNote> findAllByCategory_NameAndClient_IdAndNoteDateBetween(
            String name, Long id, LocalDate start, LocalDate end, Pageable pageable);

    List<CaseNote> findAllByCategory_NameAndClient_Id(String name, Long id, Pageable pageable);
}
