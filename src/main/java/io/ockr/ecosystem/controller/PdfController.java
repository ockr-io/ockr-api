package io.ockr.ecosystem.controller;

import io.ockr.ecosystem.algorithm.DefaultPuzzleAlgorithm;
import io.ockr.ecosystem.algorithm.PuzzlePingPongAlgorithm;
import io.ockr.ecosystem.entity.HashResult;
import io.ockr.ecosystem.entity.Model;
import io.ockr.ecosystem.entity.TextPosition;
import io.ockr.ecosystem.service.ModelService;
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

    @Autowired
    private ModelService modelService;

    @PostMapping("/create/qrcode")
    public ResponseEntity<?> createOckrQrCode(@RequestParam("file") MultipartFile file) {
        if (file.getContentType() != null && !file.getContentType().equals("application/pdf")) {
            return ResponseEntity.badRequest().body("The provided file is not a PDF file");
        }

        List<TextPosition> textPositions;
        String base64Image;
        try {
            textPositions = new ArrayList<>(pdfService.extractTextPositions(file.getInputStream()));
            base64Image = pdfService.pageToBase64Image(file.getInputStream(), 0);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Could not read PDF file");
        }

        if (textPositions.size() == 0 || base64Image.length() == 0) {
            return ResponseEntity.badRequest().body("PDF file does not contain any text");
        }

        PuzzlePingPongAlgorithm puzzlePingPongAlgorithm = new PuzzlePingPongAlgorithm(modelService);

        HashResult hashResult;
        try {
            hashResult = puzzlePingPongAlgorithm.compute(textPositions, base64Image);
        } catch (IllegalArgumentException exception) {
            String modelName = puzzlePingPongAlgorithm.getStringParameter("modelName");
            String modelVersion = puzzlePingPongAlgorithm.getStringParameter("modelVersion");

            return ResponseEntity.status(404).body("There is no model available matching the given name and version " +
                    "(" + modelName + ", " + modelVersion + "). " +
                    "If you did not specify a name nor version, the default model for this algorithm has not been " +
                    "registered yet. please contact the support or try again later.");
        }

        return ResponseEntity.ok(hashResult.toString());
    }
}
