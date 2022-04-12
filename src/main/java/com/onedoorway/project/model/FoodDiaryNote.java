package com.onedoorway.project.model;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
        name = "food_diary_notes",
        uniqueConstraints = {
            @UniqueConstraint(columnNames = {"client_id", "report_date", "meal_type"})
        })
public class FoodDiaryNote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "food_diary_id")
    private long id;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Column(name = "meal_type")
    private MealType mealType;

    @Column(name = "meal_time")
    private LocalDateTime mealTime;

    @Column(name = "meal_food")
    private String mealFood;

    @Column(name = "meal_drink")
    private String mealDrink;

    @Column(name = "meal_comments")
    private String mealComments;

    @Column(name = "meal_by")
    private String mealUpdatedBy;

    @Column(name = "report_date")
    private LocalDate reportDate;

    @Column(name = "last_updated_at")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant lastUpdatedAt;
}
