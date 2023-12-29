package io.ockr.ecosystem.algorithm;

import io.ockr.ecosystem.entity.HashResult;
import io.ockr.ecosystem.entity.PuzzlePiece;
import io.ockr.ecosystem.entity.TextPosition;
import java.util.ArrayList;
import java.util.List;

public class DefaultPuzzleAlgorithm extends Algorithm {

    public DefaultPuzzleAlgorithm() {
        super("default-puzzle");
        Parameter xSlice = Parameter.builder()
                .name("xSlice")
                .type(ParameterType.INTEGER)
                .description("The number of slices in the x direction")
                .defaultValue("3")
                .build();
        Parameter ySlice = Parameter.builder()
                .name("ySlice")
                .type(ParameterType.INTEGER)
                .description("The number of slices in the y direction")
                .defaultValue("3")
                .build();
        this.parameters.addAll(List.of(xSlice, ySlice));
    }

    @Override
    protected double error(List<TextPosition> inferenceResult, List<TextPosition> groundTruth) {
        return 0;
    }

    @Override
    public HashResult compute(List<TextPosition> textPositions, String base64Image) {
        int sliceX = getIntegerParameter("xSlice");
        int sliceY = getIntegerParameter("ySlice");
        double minX = textPositions.stream().mapToDouble(TextPosition::getX).min().orElse(0);
        double maxX = textPositions.stream().mapToDouble(TextPosition::getX).max().orElse(0);
        double minY = textPositions.stream().mapToDouble(TextPosition::getY).min().orElse(0);
        double maxY = textPositions.stream().mapToDouble(TextPosition::getY).max().orElse(0);
        double areaWidth = maxX - minX;
        double areaHeight = maxY - minY;
        double sliceWidth = areaWidth / sliceX;
        double sliceHeight = areaHeight / sliceY;

        HashResult result = new HashResult();
        result.setAlgorithm(this.getName());
        result.setParameters(this.getParameters());
        String text = textPositions.stream()
                .reduce("", (s, textPosition) -> s + textPosition.getText(), String::concat);
        result.setHash(this.hash(text));

        List<PuzzlePiece> puzzlePieces = new ArrayList<>();

        for (int i = 0; i < sliceX; i++) {
            for (int j =0; j < sliceY; j++) {
                double puzzlePositionX = i * sliceWidth;
                double puzzlePositionY = j * sliceHeight;
                List<TextPosition> textUnderPuzzleArea = textPositions.stream()
                        .filter(textPosition -> textPosition.getX() >= puzzlePositionX && textPosition.getX() <= puzzlePositionX + sliceWidth)
                        .filter(textPosition -> textPosition.getY() >= puzzlePositionY && textPosition.getY() <= puzzlePositionY + sliceHeight)
                        .toList();
                String puzzleText = textUnderPuzzleArea.stream()
                        .reduce("", (s, textPosition) -> s + textPosition.getText(), String::concat);
                puzzlePieces.add(PuzzlePiece.builder()
                        .textPositions(textUnderPuzzleArea)
                        .hash(this.hash(puzzleText))
                        .x(i)
                        .y(j)
                        .width(sliceWidth)
                        .height(sliceHeight)
                        .build());
            }
        }

        result.setPuzzlePieces(puzzlePieces);

        result.setGridWidth(areaWidth);
        result.setGridHeight(areaHeight);
        result.setPuzzleWidth(sliceWidth);
        result.setPuzzleHeight(sliceHeight);

        if (result.toString().length() > MAX_QR_CODE_CHARS) {
            throw new RuntimeException("The QR code content is too big. The limit is " +
                    MAX_QR_CODE_CHARS + " characters and the current content has " +
                    result.toString().length() + " characters.");
        }

        return result;
    }
}
