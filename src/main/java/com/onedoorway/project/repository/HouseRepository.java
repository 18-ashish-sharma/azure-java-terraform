package com.onedoorway.project.repository;

import com.onedoorway.project.model.House;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HouseRepository extends JpaRepository<House, Long> {
    House getByHouseCode(String houseCode);

    List<House> findAllByDeleted(Boolean deleted);
}
