package com.example.resumeanalyzer.dto;

import java.util.List;

public record AiFeedback(String feedback, List<String> interviewQuestions) {}