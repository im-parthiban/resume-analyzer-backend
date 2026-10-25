package com.example.resumeanalyzer.controller;

import com.example.resumeanalyzer.dto.AnalysisRequest;
import com.example.resumeanalyzer.dto.AnalysisResponse;
import com.example.resumeanalyzer.service.AnalysisService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/analyses")
public class AnalysisController {

    private final AnalysisService analysisService;

    public AnalysisController(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    @PostMapping
    public ResponseEntity<AnalysisResponse> analyze(@Valid @RequestBody AnalysisRequest request,
                                                    Authentication authentication) {
        AnalysisResponse response = analysisService.analyze(authentication.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<AnalysisResponse>> list(Authentication authentication) {
        return ResponseEntity.ok(analysisService.listForUser(authentication.getName()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnalysisResponse> get(@PathVariable Long id,
                                                Authentication authentication) {
        return ResponseEntity.ok(analysisService.getForUser(authentication.getName(), id));
    }
}