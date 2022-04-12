package com.onedoorway.project.repository;

import com.onedoorway.project.model.HouseImage;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HouseImageRepository extends JpaRepository<HouseImage, Long> {
    Optional<HouseImage> findByHouseCode(String houseCode);
}
