package com.company.foodapp.repositories;

import com.company.foodapp.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByValidationCode(String validationCode);
}
