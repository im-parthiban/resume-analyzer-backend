package com.example.resumeanalyzer.service;

import com.example.resumeanalyzer.dto.AiFeedback;
import com.example.resumeanalyzer.dto.MatchResult;

public interface AiFeedbackService {

    AiFeedback generate(String resumeText, String jobDescriptionText, MatchResult match);
}