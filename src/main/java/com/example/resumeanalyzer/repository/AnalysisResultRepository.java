package com.example.resumeanalyzer.repository;

import com.example.resumeanalyzer.entity.AnalysisResult;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface AnalysisResultRepository extends JpaRepository<AnalysisResult, Long> {

    @EntityGraph(attributePaths = {"jobDescription", "resume"})
    List<AnalysisResult> findByUserIdOrderByCreatedAtDesc(Long userId);

    @EntityGraph(attributePaths = {"jobDescription", "resume"})
    Optional<AnalysisResult> findByIdAndUserId(Long id, Long userId);
}