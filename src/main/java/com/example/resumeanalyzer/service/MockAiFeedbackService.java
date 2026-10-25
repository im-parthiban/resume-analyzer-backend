package com.example.resumeanalyzer.service;

import com.example.resumeanalyzer.dto.AiFeedback;
import com.example.resumeanalyzer.dto.MatchResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MockAiFeedbackService implements AiFeedbackService {

    @Override
    public AiFeedback generate(String resumeText, String jobDescriptionText, MatchResult match) {
        StringBuilder feedback = new StringBuilder();
        feedback.append("Your resume matches ")
                .append(match.matchedSkills().size())
                .append(" of ")
                .append(match.requiredSkillCount())
                .append(" skills required for this role (score: ")
                .append(match.score())
                .append("%). ");

        if (match.missingSkills().isEmpty()) {
            feedback.append("You cover every skill the job lists. Focus on showing measurable results in your projects.");
        } else {
            feedback.append("To improve your match, add projects or experience that show: ")
                    .append(String.join(", ", match.missingSkills()))
                    .append(". Also describe your results with numbers where possible.");
        }

        List<String> questions = new ArrayList<>();
        match.matchedSkills().stream().limit(4).forEach(skill ->
                questions.add("Explain how you have used " + skill + " in a project."));
        match.missingSkills().stream().limit(2).forEach(skill ->
                questions.add("You have not listed " + skill + ". How would you learn it quickly?"));

        return new AiFeedback(feedback.toString(), questions);
    }
}