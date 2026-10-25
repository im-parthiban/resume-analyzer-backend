package com.example.resumeanalyzer.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SkillExtractorTest {

    private SkillExtractor extractor;

    @BeforeEach
    void setUp() {
        extractor = new SkillExtractor();
        extractor.loadSkills();
    }

    @Test
    void findsBasicSkills() {
        Set<String> found = extractor.extractSkills("Java developer with Spring Boot and MySQL");
        assertEquals(Set.of("Java", "Spring Boot", "MySQL"), found);
    }

    @Test
    void javaIsNotFoundInsideJavaScript() {
        Set<String> found = extractor.extractSkills("Experienced in JavaScript and React");
        assertEquals(Set.of("JavaScript", "React"), found);
    }

    @Test
    void sqlIsNotFoundInsideMySqlOrNoSql() {
        Set<String> found = extractor.extractSkills("MySQL and NoSQL");
        assertEquals(Set.of("MySQL", "NoSQL"), found);
    }

    @Test
    void findsPlainSql() {
        Set<String> found = extractor.extractSkills("Strong SQL skills");
        assertEquals(Set.of("SQL"), found);
    }

    @Test
    void alternateSpellingsMapToOfficialNames() {
        Set<String> found = extractor.extractSkills("springboot, restful apis, k8s");
        assertEquals(Set.of("Spring Boot", "REST API", "Kubernetes"), found);
    }

    @Test
    void handlesSpecialCharactersInSkillNames() {
        Set<String> found = extractor.extractSkills("C++ and C# and .NET and CI/CD");
        assertEquals(Set.of("C++", "C#", ".NET", "CI/CD"), found);
    }

    @Test
    void ignoresCase() {
        Set<String> found = extractor.extractSkills("JAVA and python");
        assertEquals(Set.of("Java", "Python"), found);
    }

    @Test
    void findsSkillsSplitAcrossLines() {
        Set<String> found = extractor.extractSkills("Spring\nBoot developer");
        assertEquals(Set.of("Spring Boot"), found);
    }

    @Test
    void returnsEmptySetForNullOrBlankText() {
        assertTrue(extractor.extractSkills(null).isEmpty());
        assertTrue(extractor.extractSkills("   ").isEmpty());
    }
}