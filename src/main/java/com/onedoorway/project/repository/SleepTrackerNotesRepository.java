package com.onedoorway.project.repository;

import com.onedoorway.project.model.SleepTrackerNotes;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SleepTrackerNotesRepository extends JpaRepository<SleepTrackerNotes, Long> {
    SleepTrackerNotes findByClient_IdAndReportDate(long id, LocalDate reportDate);
}
