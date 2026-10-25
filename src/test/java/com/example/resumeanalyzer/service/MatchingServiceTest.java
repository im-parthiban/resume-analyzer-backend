package com.example.resumeanalyzer.service;

import com.example.resumeanalyzer.dto.MatchResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MatchingServiceTest {

    private MatchingService matchingService;

    @BeforeEach
    void setUp() {
        SkillExtractor extractor = new SkillExtractor();
        extractor.loadSkills();
        matchingService = new MatchingService(extractor);
    }

    @Test
    void perfectMatchScoresHundred() {
        MatchResult result = matchingService.match(
                "Java, Spring Boot and MySQL developer",
                "Required: Java, Spring Boot, MySQL");

        assertEquals(100, result.score());
        assertEquals(3, result.requiredSkillCount());
        assertEquals(Set.of("Java", "Spring Boot", "MySQL"), Set.copyOf(result.matchedSkills()));
        assertEquals(0, result.missingSkills().size());
    }

    @Test
    void partialMatchListsMatchedAndMissingSkills() {
        MatchResult result = matchingService.match(
                "Java and Spring Boot developer",
                "Looking for Java, Spring Boot, MySQL and Docker");

        assertEquals(50, result.score());
        assertEquals(4, result.requiredSkillCount());
        assertEquals(Set.of("Java", "Spring Boot"), Set.copyOf(result.matchedSkills()));
        assertEquals(Set.of("MySQL", "Docker"), Set.copyOf(result.missingSkills()));
    }

    @Test
    void noOverlapScoresZero() {
        MatchResult result = matchingService.match(
                "Python and Pandas",
                "Java, Spring Boot");

        assertEquals(0, result.score());
        assertEquals(0, result.matchedSkills().size());
        assertEquals(Set.of("Java", "Spring Boot"), Set.copyOf(result.missingSkills()));
    }

    @Test
    void extraResumeSkillsDoNotChangeTheScore() {
        MatchResult result = matchingService.match(
                "Java, Python, Docker, AWS, Git",
                "Java and Docker");

        assertEquals(100, result.score());
        assertEquals(2, result.requiredSkillCount());
    }

    @Test
    void scoreIsRoundedToNearestWholeNumber() {
        MatchResult result = matchingService.match(
                "Java and Docker",
                "Java, Spring Boot, Docker");

        assertEquals(67, result.score());
    }

    @Test
    void javaScriptDoesNotCountAsJava() {
        MatchResult result = matchingService.match(
                "JavaScript and React developer",
                "Java developer needed");

        assertEquals(0, result.score());
        assertEquals(Set.of("Java"), Set.copyOf(result.missingSkills()));
    }

    @Test
    void jobDescriptionWithNoKnownSkillsIsRejected() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                matchingService.match(
                        "Java developer",
                        "We are looking for a friendly and hardworking person"));

        assertEquals(422, exception.getStatusCode().value());
    }
}