package com.example.gymprogress.controllers;

import com.example.gymprogress.entities.Progress;
import com.example.gymprogress.repositories.ProgressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@RestController
@RequestMapping("/api/progress")
public class ProgressController {

    @Autowired
    private ProgressRepository progressRepository;

    @GetMapping
    public List<Progress> getProgress() {
        return progressRepository.findAll();
    }

    @PostMapping
    public Progress addProgress(@RequestBody Progress progress) {
        return progressRepository.save(progress);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Progress> updateProgress(@PathVariable Long id, @RequestBody Progress updatedProgress) {
        return progressRepository.findById(id)
                .map(progress -> {
                    progress.setDate(updatedProgress.getDate());
                    progress.setWeight(updatedProgress.getWeight());
                    progress.setReps(updatedProgress.getReps());
                    return ResponseEntity.ok(progressRepository.save(progress));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteProgress(@PathVariable Long id) {
        return progressRepository.findById(id)
                .map(progress -> {
                    progressRepository.delete(progress);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }


}
