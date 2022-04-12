package com.onedoorway.project.repository;

import com.onedoorway.project.model.Client;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    List<Client> findByHouse_HouseCode(String houseCode);

    List<Client>
            findByNameContainingIgnoreCaseAndDeletedOrHouse_HouseCodeContainingIgnoreCaseAndDeleted(
                    String name,
                    Boolean deleted1,
                    String houseCode,
                    Boolean deleted2,
                    Pageable pageable);

    List<Client> findAllByDeleted(Boolean deleted, Pageable pageable);

    Client getByName(String name);

    Long countByDeleted(Boolean deleted);

    Long countByHouse_HouseCodeAndDeleted(String houseCode, Boolean deleted);

    Long countByNameContainingIgnoreCaseAndDeletedOrHouse_HouseCodeContainingIgnoreCaseAndDeleted(
            String name, Boolean deleted1, String houseCode, Boolean deleted2);
}
