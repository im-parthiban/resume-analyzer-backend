package com.example.resumeanalyzer.service;

import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class SkillExtractor {

    private final Map<String, Pattern> skillPatterns = new LinkedHashMap<>();

    @PostConstruct
    void loadSkills() {
        ClassPathResource resource = new ClassPathResource("skills.txt");

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                String[] names = line.split("\\|");
                String canonical = names[0].trim();

                String alternatives = Arrays.stream(names)
                        .map(String::trim)
                        .filter(name -> !name.isEmpty())
                        .sorted(Comparator.comparingInt(String::length).reversed())
                        .map(Pattern::quote)
                        .collect(Collectors.joining("|"));

                Pattern pattern = Pattern.compile(
                        "(?<![A-Za-z0-9.])(?:" + alternatives + ")(?![A-Za-z0-9+#])",
                        Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

                skillPatterns.put(canonical, pattern);
            }

        } catch (IOException e) {
            throw new IllegalStateException("Could not load skills.txt", e);
        }
    }

    public Set<String> extractSkills(String text) {
        Set<String> found = new LinkedHashSet<>();
        if (text == null || text.isBlank()) {
            return found;
        }

        String normalized = text.replaceAll("\\s+", " ");

        for (Map.Entry<String, Pattern> entry : skillPatterns.entrySet()) {
            if (entry.getValue().matcher(normalized).find()) {
                found.add(entry.getKey());
            }
        }
        return found;
    }
}