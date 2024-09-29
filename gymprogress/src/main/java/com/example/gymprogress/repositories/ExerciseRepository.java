package com.example.gymprogress.repositories;

import com.example.gymprogress.entities.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    // Možeš dodati metode za pronalaženje vežbi korisnika
    List<Exercise> findByUserId(Long userId);
}
