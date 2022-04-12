package com.onedoorway.project.repository;

import com.onedoorway.project.model.FoodDiaryNote;
import com.onedoorway.project.model.MealType;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodDiaryNoteRepository extends JpaRepository<FoodDiaryNote, Long> {
    FoodDiaryNote findByClient_IdAndMealTypeAndReportDate(
            long id, MealType mealType, LocalDate reportDate);
}
