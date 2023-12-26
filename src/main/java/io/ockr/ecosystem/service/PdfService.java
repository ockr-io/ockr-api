package io.ockr.ecosystem.service;

import io.ockr.ecosystem.entity.TextPosition;
import io.ockr.ecosystem.format.pdf.PDFTextPositionStripper;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
public class PdfService {

    public List<TextPosition> extractTextPositions(InputStream inputStream) throws IOException {
        PDDocument document = Loader.loadPDF(inputStream.readAllBytes());
        return extractTextPositions(document);
    }

    public List<TextPosition> extractTextPositions(File file) throws IOException {
        PDDocument document = Loader.loadPDF(file);
        return extractTextPositions(document);
    }

    public String pageToBase64Image(InputStream inputStream, int pageIndex) throws IOException {
        PDDocument document = Loader.loadPDF(inputStream.readAllBytes());
        PDFRenderer pdfRenderer = new PDFRenderer(document);
        PDPage page = document.getPage(pageIndex);

        if (page != null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(pageIndex, 300, ImageType.RGB);
            ImageIO.write(bufferedImage, "jpg", byteArrayOutputStream);
            return Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
        } else {
            return null;
        }
    }

    public List<TextPosition> extractTextPositions(PDDocument document) throws IOException {

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
