package com.example.resumeanalyzer.dto;

import com.example.resumeanalyzer.entity.AnalysisResult;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public record AnalysisResponse(
        Long id,
        String jobTitle,
        String companyName,
        String resumeFileName,
        int matchScore,
        int requiredSkillCount,
        List<String> matchedSkills,
        List<String> missingSkills,
        String aiFeedback,
        String interviewQuestions,
        LocalDateTime createdAt
) {
    public static AnalysisResponse from(AnalysisResult result) {
        List<String> matched = split(result.getMatchedSkills());
        List<String> missing = split(result.getMissingSkills());

        return new AnalysisResponse(
                result.getId(),
                result.getJobDescription().getJobTitle(),
                result.getJobDescription().getCompanyName(),
                result.getResume().getFileName(),
                result.getMatchScore(),
                matched.size() + missing.size(),
                matched,
                missing,
                result.getAiFeedback(),
                result.getInterviewQuestions(),
                result.getCreatedAt()
        );
    }

    private static List<String> split(String value) {
        if (value == null || value.isBlank()) {
            return List.of();
        }
        return Arrays.stream(value.split(",")).map(String::trim).toList();
    }
}