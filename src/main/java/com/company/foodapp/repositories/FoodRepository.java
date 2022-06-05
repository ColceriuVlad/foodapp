package com.company.foodapp.repositories;

import com.company.foodapp.models.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface FoodRepository extends JpaRepository<Food, Integer> {
    Optional<Food> findByName(String name);
}
