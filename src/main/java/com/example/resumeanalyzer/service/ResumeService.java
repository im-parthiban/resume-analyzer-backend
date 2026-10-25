package com.example.resumeanalyzer.service;

import com.example.resumeanalyzer.dto.ResumeResponse;
import com.example.resumeanalyzer.entity.Resume;
import com.example.resumeanalyzer.entity.User;
import com.example.resumeanalyzer.repository.ResumeRepository;
import com.example.resumeanalyzer.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final FileParserService fileParserService;

    public ResumeService(ResumeRepository resumeRepository,
                         UserRepository userRepository,
                         FileParserService fileParserService) {
        this.resumeRepository = resumeRepository;
        this.userRepository = userRepository;
        this.fileParserService = fileParserService;
    }

    public ResumeResponse upload(String email, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please select a file");
        }

        User user = findUser(email);
        String text = fileParserService.extractText(file);
        String extension = fileParserService.getExtension(file.getOriginalFilename());

        String fileName = file.getOriginalFilename();
        if (fileName == null || fileName.isBlank()) {
            fileName = "resume." + extension;
        }
        if (fileName.length() > 255) {
            fileName = fileName.substring(0, 255);
        }

        Resume resume = Resume.builder()
                .fileName(fileName)
                .fileType(extension)
                .extractedText(text)
                .user(user)
                .build();

        return ResumeResponse.from(resumeRepository.save(resume));
    }

    public List<ResumeResponse> listForUser(String email) {
        User user = findUser(email);
        return resumeRepository.findByUserIdOrderByUploadedAtDesc(user.getId())
                .stream()
                .map(ResumeResponse::from)
                .toList();
    }

    private User findUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "User not found"));
    }
}