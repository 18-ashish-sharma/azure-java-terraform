package com.onedoorway.project.repository;

import com.onedoorway.project.model.HandoverSummary;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HandoverSummaryRepository extends JpaRepository<HandoverSummary, Long> {
    List<HandoverSummary> findAllByHouse_HouseCodeAndHandoverDateBetween(
            String houseCode, LocalDate start, LocalDate end, Pageable pageable);
}
