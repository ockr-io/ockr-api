package io.ockr.ecosystem.service;

import io.ockr.ecosystem.entity.TextPosition;
import io.ockr.ecosystem.format.pdf.PDFTextPositionStripper;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class PdfService {

    public List<TextPosition> extractTextPositions(File file) throws IOException {
        PDDocument document = Loader.loadPDF(file);
        List<TextPosition> textPositions = new ArrayList<>();

        for (int page = 1; page < document.getNumberOfPages() + 1; page++) {
            PDFTextPositionStripper pdfStripper = new PDFTextPositionStripper();
            pdfStripper.setStartPage(page);
            pdfStripper.setEndPage(page);
            pdfStripper.getText(document);
            List<TextPosition> pageTextPositions = pdfStripper.getTextPositions();
            final int pageNumber = page;
            pageTextPositions.forEach(textPosition -> textPosition.setPage(pageNumber));
            textPositions.addAll(pageTextPositions);
        }

        document.close();
        return textPositions;
    }
}
