package com.example.resumeanalyzer.service;

import com.example.resumeanalyzer.dto.AnalysisRequest;
import com.example.resumeanalyzer.dto.AnalysisResponse;
import com.example.resumeanalyzer.dto.MatchResult;
import com.example.resumeanalyzer.entity.AnalysisResult;
import com.example.resumeanalyzer.entity.JobDescription;
import com.example.resumeanalyzer.entity.Resume;
import com.example.resumeanalyzer.entity.User;
import com.example.resumeanalyzer.repository.AnalysisResultRepository;
import com.example.resumeanalyzer.repository.JobDescriptionRepository;
import com.example.resumeanalyzer.repository.ResumeRepository;
import com.example.resumeanalyzer.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.example.resumeanalyzer.dto.AiFeedback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class AnalysisService {

    private final UserRepository userRepository;
    private final ResumeRepository resumeRepository;
    private final JobDescriptionRepository jobDescriptionRepository;
    private final AnalysisResultRepository analysisResultRepository;
    private final MatchingService matchingService;


    private static final Logger log = LoggerFactory.getLogger(AnalysisService.class);

    private final AiFeedbackService aiFeedbackService;

    public AnalysisService(UserRepository userRepository,
                           ResumeRepository resumeRepository,
                           JobDescriptionRepository jobDescriptionRepository,
                           AnalysisResultRepository analysisResultRepository,
                           MatchingService matchingService,
                           AiFeedbackService aiFeedbackService) {
        this.userRepository = userRepository;
        this.resumeRepository = resumeRepository;
        this.jobDescriptionRepository = jobDescriptionRepository;
        this.analysisResultRepository = analysisResultRepository;
        this.matchingService = matchingService;
        this.aiFeedbackService = aiFeedbackService;
    }

    @Transactional
    public AnalysisResponse analyze(String email, AnalysisRequest request) {
        User user = findUser(email);

        Resume resume = resumeRepository
                .findByIdAndUserId(request.resumeId(), user.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Resume not found"));

        MatchResult match = matchingService.match(
                resume.getExtractedText(), request.descriptionText());

        String aiFeedbackText = null;
        String interviewQuestions = null;
        try {
            AiFeedback feedback = aiFeedbackService.generate(
                    resume.getExtractedText(), request.descriptionText(), match);
            aiFeedbackText = feedback.feedback();
            interviewQuestions = String.join("\n", feedback.interviewQuestions());
        } catch (Exception e) {
            log.warn("AI feedback failed, saving analysis without it: {}", e.getMessage());
        }

        JobDescription jobDescription = jobDescriptionRepository.save(
                JobDescription.builder()
                        .jobTitle(request.jobTitle().trim())
                        .companyName(blankToNull(request.companyName()))
                        .descriptionText(request.descriptionText().trim())
                        .user(user)
                        .build());

        AnalysisResult saved = analysisResultRepository.save(
                AnalysisResult.builder()
                        .matchScore(match.score())
                        .matchedSkills(String.join(",", match.matchedSkills()))
                        .missingSkills(String.join(",", match.missingSkills()))
                        .aiFeedback(aiFeedbackText)
                        .interviewQuestions(interviewQuestions)
                        .user(user)
                        .resume(resume)
                        .jobDescription(jobDescription)
                        .build());

        return AnalysisResponse.from(saved);
    }

    public List<AnalysisResponse> listForUser(String email) {
        User user = findUser(email);
        return analysisResultRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(AnalysisResponse::from)
                .toList();
    }

    public AnalysisResponse getForUser(String email, Long id) {
        User user = findUser(email);
        return analysisResultRepository.findByIdAndUserId(id, user.getId())
                .map(AnalysisResponse::from)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Analysis not found"));
    }

    private User findUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "User not found"));
    }

    private String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }
}