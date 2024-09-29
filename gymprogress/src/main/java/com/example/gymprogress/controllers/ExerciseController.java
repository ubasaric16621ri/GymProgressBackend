package com.example.gymprogress.controllers;

import com.example.gymprogress.entities.Exercise;
import com.example.gymprogress.repositories.ExerciseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exercises")
public class ExerciseController {

    @Autowired
    private ExerciseRepository exerciseRepository;

    // GET - vraća sve vežbe za autentifikovanog korisnika
    @GetMapping
    public List<Exercise> getExercises() {
        return exerciseRepository.findAll(); // Možeš dodati filtriranje po korisniku
    }

    // POST - dodaje novu vežbu
    @PostMapping
    public Exercise addExercise(@RequestBody Exercise exercise) {
        return exerciseRepository.save(exercise);
    }

    // PUT - ažurira vežbu
    @PutMapping("/{id}")
    public ResponseEntity<Exercise> updateExercise(@PathVariable Long id, @RequestBody Exercise updatedExercise) {
        return exerciseRepository.findById(id)
                .map(exercise -> {
                    exercise.setName(updatedExercise.getName());
                    exercise.setDescription(updatedExercise.getDescription());
                    exercise.setSets(updatedExercise.getSets());
                    exercise.setReps(updatedExercise.getReps());
                    exercise.setWeight(updatedExercise.getWeight());
                    return ResponseEntity.ok(exerciseRepository.save(exercise));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE - briše vežbu
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteExercise(@PathVariable Long id) {
        return exerciseRepository.findById(id)
                .map(exercise -> {
                    exerciseRepository.delete(exercise);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

}
