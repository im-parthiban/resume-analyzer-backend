package com.example.resumeanalyzer.dto;

import java.util.List;

public record MatchResult(
        int score,
        int requiredSkillCount,
        List<String> matchedSkills,
        List<String> missingSkills
) {}