package com.example.resumeanalyzer.service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

@Service
public class FileParserService {

    public String extractText(MultipartFile file) {
        String extension = getExtension(file.getOriginalFilename());

        try {
            String text = switch (extension) {
                case "pdf" -> extractFromPdf(file);
                case "docx" -> extractFromDocx(file);
                default -> throw new ResponseStatusException(
                        HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Only PDF and DOCX files are supported");
            };

            String cleaned = clean(text);
            if (cleaned.isBlank()) {
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                        "No readable text found. Scanned or image-only resumes are not supported.");
            }
            return cleaned;

        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Could not read the file. It may be corrupted or password protected.");
        }
    }

    private String extractFromPdf(MultipartFile file) throws IOException {
        try (PDDocument document = Loader.loadPDF(file.getBytes())) {
            return new PDFTextStripper().getText(document);
        }
    }

    private String extractFromDocx(MultipartFile file) throws IOException {
        try (InputStream in = file.getInputStream();
             XWPFDocument document = new XWPFDocument(in);
             XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
            return extractor.getText();
        }
    }

    public String getExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
    }

    private String clean(String text) {
        return text.replaceAll("[ \\t]+", " ")
                .replaceAll("\\n{3,}", "\n\n")
                .trim();
    }
}