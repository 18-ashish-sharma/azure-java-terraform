package com.onedoorway.project.repository;

import com.onedoorway.project.model.HouseContact;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HouseContactRepository extends JpaRepository<HouseContact, Long> {
    List<HouseContact> findAllByHouses_HouseCode(Pageable pageable, String houseCode);

    List<HouseContact> findAllByHouses_HouseCodeIn(List<String> houseCode);
}
