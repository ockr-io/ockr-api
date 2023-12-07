package io.ockr.ecosystem.controller;

import io.ockr.ecosystem.algorithm.DefaultPuzzleAlgorithm;
import io.ockr.ecosystem.entity.HashResult;
import io.ockr.ecosystem.entity.TextPosition;
import io.ockr.ecosystem.service.PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/pdf/")
public class PdfController {

    @Autowired
    private PdfService pdfService;

    @PostMapping("/create/qrcode")
    public ResponseEntity<?> createOckrQrCode(@RequestParam("file") MultipartFile file) {
        if (file.getContentType() != null && !file.getContentType().equals("application/pdf")) {
            return ResponseEntity.badRequest().body("The provided file is not a PDF file");
        }

        List<TextPosition> textPositions;
        try {
            textPositions = new ArrayList<>(pdfService.extractTextPositions(file.getInputStream()));
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Could not read PDF file");
        }

        if (textPositions.size() == 0) {
            return ResponseEntity.badRequest().body("PDF file does not contain any text");
        }

        DefaultPuzzleAlgorithm defaultPuzzleAlgorithm = new DefaultPuzzleAlgorithm();
        HashResult hashResult = defaultPuzzleAlgorithm.compute(textPositions);

        return ResponseEntity.ok(hashResult.toString());
    }

}