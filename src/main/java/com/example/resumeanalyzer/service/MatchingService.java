package com.example.resumeanalyzer.service;

import com.example.resumeanalyzer.dto.MatchResult;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class MatchingService {

    private final SkillExtractor skillExtractor;

    public MatchingService(SkillExtractor skillExtractor) {
        this.skillExtractor = skillExtractor;
    }

    public MatchResult match(String resumeText, String jobDescriptionText) {
        Set<String> resumeSkills = skillExtractor.extractSkills(resumeText);
        Set<String> requiredSkills = skillExtractor.extractSkills(jobDescriptionText);

        if (requiredSkills.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "No known skills were found in the job description");
        }

        Set<String> matched = new LinkedHashSet<>(requiredSkills);
        matched.retainAll(resumeSkills);

        Set<String> missing = new LinkedHashSet<>(requiredSkills);
        missing.removeAll(resumeSkills);

        int score = (int) Math.round(matched.size() * 100.0 / requiredSkills.size());

        return new MatchResult(
                score,
                requiredSkills.size(),
                new ArrayList<>(matched),
                new ArrayList<>(missing)
        );
    }
}