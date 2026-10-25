package com.example.resumeanalyzer.dto;

import com.example.resumeanalyzer.entity.Resume;
import java.time.LocalDateTime;

public record ResumeResponse(
        Long id,
        String fileName,
        String fileType,
        int textLength,
        String preview,
        LocalDateTime uploadedAt
) {
    public static ResumeResponse from(Resume resume) {
        String text = resume.getExtractedText();
        String preview = text.length() > 300 ? text.substring(0, 300) + "..." : text;
        return new ResumeResponse(
                resume.getId(),
                resume.getFileName(),
                resume.getFileType(),
                text.length(),
                preview,
                resume.getUploadedAt()
        );
    }
}