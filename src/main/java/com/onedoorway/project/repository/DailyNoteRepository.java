package com.onedoorway.project.repository;

import com.onedoorway.project.model.DailyNote;
import java.time.Instant;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DailyNoteRepository extends JpaRepository<DailyNote, Long> {
    List<DailyNote> findByHouse_HouseCode(String houseCode, Sort sort);

    List<DailyNote> findByHouse_HouseCodeAndCreateBy_Id(String houseCode, Long id);

    List<DailyNote> findAllByHouse_HouseCodeAndClient_IdAndCreatedAtBetween(
            String houseCode, Long id, Instant startDate, Instant endDate, Pageable pageable);
}
