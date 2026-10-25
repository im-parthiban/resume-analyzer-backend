package com.example.resumeanalyzer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AnalysisRequest(

        @NotNull(message = "Resume is required")
        Long resumeId,

        @NotBlank(message = "Job title is required")
        @Size(max = 255, message = "Job title is too long")
        String jobTitle,

        @Size(max = 255, message = "Company name is too long")
        String companyName,

        @NotBlank(message = "Job description is required")
        @Size(max = 20000, message = "Job description is too long")
        String descriptionText
) {}