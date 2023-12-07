package io.ockr.ecosystem.format.pdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PDFTextPositionStripper extends PDFTextStripper {

    private static final String LATEX_DOUBLE_F_CHAR = String.valueOf((char) 0x21B5);

    private final List<io.ockr.ecosystem.entity.TextPosition> textPositions;

    public PDFTextPositionStripper() throws IOException {
        super();
        this.textPositions = new ArrayList<>();
    }

    public List<io.ockr.ecosystem.entity.TextPosition> getTextPositions() {
        return textPositions;
    }

    @Override
    protected void writeString(String string, List<TextPosition> textPositions) throws IOException {
        Float minX = textPositions.stream().map(TextPosition::getX).min(Float::compareTo).orElse(0f);
        Float minY = textPositions.stream().map(TextPosition::getY).min(Float::compareTo).orElse(0f);
        Float maxX = textPositions.stream().map(TextPosition::getEndX).max(Float::compareTo).orElse(0f);
        Float maxY = textPositions.stream().map(TextPosition::getEndY).max(Float::compareTo).orElse(0f);
        double width = maxX - minX;
        double height = maxY - minY;

        // LaTeX files use a single character to represent a double "ff"
        string = string.replace(LATEX_DOUBLE_F_CHAR, "ff");

        this.textPositions.add(io.ockr.ecosystem.entity.TextPosition.builder()
                .text(string)
                .x(minX.doubleValue())
                .y(minY.doubleValue())
                .width(width)
                .height(height)
                .build());

        super.writeString(string, textPositions);
    }

    @Override
    public String getText(PDDocument document) throws IOException {
        this.textPositions.clear();
        return super.getText(document);
    }
}
