package com.onedoorway.project.repository;

import com.onedoorway.project.model.NightReport;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NightReportRepository extends JpaRepository<NightReport, Long> {
    NightReport findByClient_IdAndReportDate(long id, LocalDate reportDate);
}
