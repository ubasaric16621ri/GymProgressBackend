package com.example.gymprogress.repositories;

import com.example.gymprogress.entities.Workout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkoutRepository extends JpaRepository<Workout, Long> {
    // Možeš dodati metode za pronalaženje treninga korisnika
    List<Workout> findByUserId(Long userId);
}
