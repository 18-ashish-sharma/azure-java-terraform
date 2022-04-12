package com.onedoorway.project.repository;

import com.onedoorway.project.model.User;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User getByEmail(String email);

    User findByEmailIgnoreCase(String email);

    List<User> findAllByDeleted(Boolean deleted, Pageable pageable);

    List<User> findByHouses_HouseCodeAndDeleted(
            String houseCode, Boolean deleted, Pageable pageable);

    Long countByDeleted(Boolean deleted);

    User getById(long id);

    List<User> findByHouses_HouseCodeContainingIgnoreCaseAndDeleted(
            String houseCode, Boolean deleted, Pageable pageable);

    List<User>
            findByFirstNameContainingIgnoreCaseAndHouses_HouseCodeContainingIgnoreCaseAndDeletedOrLastNameContainingIgnoreCaseAndHouses_HouseCodeContainingIgnoreCaseAndDeletedOrEmailContainingIgnoreCaseAndHouses_HouseCodeContainingIgnoreCaseAndDeleted(
                    String firstName,
                    String houseCode1,
                    Boolean deleted1,
                    String lastName,
                    String houseCode2,
                    Boolean deleted2,
                    String email,
                    String houseCode3,
                    Boolean deleted3,
                    Pageable pageable);

    List<User>
            findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCaseAndDeleted(
                    String firstName,
                    String lastName,
                    String email,
                    Boolean deleted,
                    Pageable pageable);

    Long
            countByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCaseAndDeleted(
                    String firstName, String lastName, String email, Boolean deleted);

    Long countByHouses_HouseCodeContainingIgnoreCaseAndDeleted(String houseCode, Boolean deleted);

    Long
            countByFirstNameContainingIgnoreCaseAndHouses_HouseCodeContainingIgnoreCaseAndDeletedOrLastNameContainingIgnoreCaseAndHouses_HouseCodeContainingIgnoreCaseAndDeletedOrEmailContainingIgnoreCaseAndHouses_HouseCodeContainingIgnoreCaseAndDeleted(
                    String firstName,
                    String houseCode1,
                    Boolean deleted1,
                    String lastName,
                    String houseCode2,
                    Boolean deleted2,
                    String email,
                    String houseCode3,
                    Boolean deleted3);

    Long countByHouses_HouseCodeAndDeleted(String houseCode, Boolean deleted);
}
