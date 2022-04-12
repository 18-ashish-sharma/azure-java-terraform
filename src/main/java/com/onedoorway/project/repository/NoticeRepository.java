package com.onedoorway.project.repository;

import com.onedoorway.project.model.House;
import com.onedoorway.project.model.Notice;
import com.onedoorway.project.model.NoticeStatus;
import java.time.Instant;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {

    @Query(
            "select n from notices n where n.noticeStatus = :status and :house member n.houses and ((n.startDate < :now and n.endDate >= :now) or (n.startDate is null and n.endDate >= :now))")
    List<Notice> findNotices(Pageable pageable, NoticeStatus status, House house, Instant now);
}
