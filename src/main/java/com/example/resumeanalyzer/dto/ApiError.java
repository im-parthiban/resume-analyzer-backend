package com.example.resumeanalyzer.dto;

import java.time.LocalDateTime;
import java.util.Map;

public record ApiError(
        LocalDateTime timestamp,
        int status,
        String message,
        Map<String, String> fieldErrors
) {}