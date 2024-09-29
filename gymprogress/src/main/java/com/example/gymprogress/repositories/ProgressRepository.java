package com.example.gymprogress.repositories;

import com.example.gymprogress.entities.Progress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProgressRepository extends JpaRepository<Progress, Long> {
    // Možeš dodati metode za pronalaženje napretka korisnika
    List<Progress> findByUserId(Long userId);
}
