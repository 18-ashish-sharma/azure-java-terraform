package com.onedoorway.project.repository;

import com.onedoorway.project.model.MiscellaneousNote;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MiscellaneousNoteRepository extends JpaRepository<MiscellaneousNote, Long> {
    List<MiscellaneousNote> findAllByCategory_NameAndNoteDateBetween(
            String name, LocalDate start, LocalDate end, Pageable pageable);
}
