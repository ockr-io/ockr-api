package io.ockr.ecosystem.algorithm;

import io.ockr.ecosystem.entity.HashResult;
import io.ockr.ecosystem.entity.PuzzlePiece;
import io.ockr.ecosystem.entity.TextPosition;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public abstract class Algorithm {
    private final String name;
    protected final List<Parameter> parameters;
    protected final int MAX_QR_CODE_CHARS = 7089;

    public Algorithm(String name) {
        this.name = name;
        this.parameters = new ArrayList<>();
    }

    public String getName() {
        return this.name;
    }

    public List<Parameter> getParameters() {
        return this.parameters;
    }

    public String getStringParameter(String name) {
        for (Parameter parameter : this.parameters) {
            if (parameter.getName().equals(name) && parameter.getType() == ParameterType.STRING) {
                if (parameter.getValue() == null) {
                    return parameter.getDefaultValue();
                }
                return parameter.getValue();
            }
        }
        throw new IllegalArgumentException("Parameter " + name + " does not exist or is not a string");
    }

    protected abstract double error(List<TextPosition> inferenceResult, List<TextPosition> groundTruth);

    private PuzzlePiece getLeftPuzzlePiece(PuzzlePiece puzzlePiece, List<PuzzlePiece> puzzlePieces) {
        if (puzzlePiece.getX() == 0) {
            return null;
        }
        List<PuzzlePiece> leftPuzzlePieces = puzzlePieces.stream().filter(piece -> piece.getX() < puzzlePiece.getX() && piece.getY() == puzzlePiece.getY()).toList();
        if (leftPuzzlePieces.size() == 0) {
            return null;
        }
        return leftPuzzlePieces.stream().max(Comparator.comparingDouble(PuzzlePiece::getX)).orElse(null);
    }

    private PuzzlePiece getRightPuzzlePiece(PuzzlePiece puzzlePiece, List<PuzzlePiece> puzzlePieces) {
        List<PuzzlePiece> rightPuzzlePieces = puzzlePieces.stream().filter(piece -> piece.getX() > puzzlePiece.getX() && piece.getY() == puzzlePiece.getY()).toList();
        if (rightPuzzlePieces.size() == 0) {
            return null;
        }
        return rightPuzzlePieces.stream().min(Comparator.comparingDouble(PuzzlePiece::getX)).orElse(null);
    }

    private PuzzlePiece getTopPuzzlePiece(PuzzlePiece puzzlePiece, List<PuzzlePiece> puzzlePieces) {
        if (puzzlePiece.getY() == 0) {
            return null;
        }
        List<PuzzlePiece> topPuzzlePieces = puzzlePieces.stream().filter(piece -> piece.getY() < puzzlePiece.getY() && piece.getX() == puzzlePiece.getX()).toList();
        if (topPuzzlePieces.size() == 0) {
            return null;
        }
        return topPuzzlePieces.stream().max(Comparator.comparingDouble(PuzzlePiece::getY)).orElse(null);
    }

    private PuzzlePiece getBottomPuzzlePiece(PuzzlePiece puzzlePiece, List<PuzzlePiece> puzzlePieces) {
        List<PuzzlePiece> bottomPuzzlePieces = puzzlePieces.stream().filter(piece -> piece.getY() > puzzlePiece.getY() && piece.getX() == puzzlePiece.getX()).toList();
        if (bottomPuzzlePieces.size() == 0) {
            return null;
        }
        return bottomPuzzlePieces.stream().min(Comparator.comparingDouble(PuzzlePiece::getY)).orElse(null);
    }

    private boolean checkMerge(PuzzlePiece origin, PuzzlePiece target) {
        if (origin.getTextPositions().size() == 0) {
            return true;
        }

        if (origin.getTextPositions().size() == target.getTextPositions().size()) {
            return origin.getTextPositions().stream().allMatch(
                    textPosition -> target.getTextPositions().stream()
                            .anyMatch(textPosition::equals));
        }

        return false;
    }

    private boolean mergeLeftIfPossible(PuzzlePiece origin, PuzzlePiece target, List<PuzzlePiece> puzzlePieces) {
        if (checkMerge(origin, target)) {
            if (origin.getHeight() == target.getHeight()) {
                target.setWidth(target.getWidth() + origin.getWidth());
                puzzlePieces.remove(origin);
                return true;
            }
        }
        return false;
    }

    private boolean mergeRightIfPossible(PuzzlePiece origin, PuzzlePiece target, List<PuzzlePiece> puzzlePieces) {
        if (checkMerge(origin, target)) {
            if (origin.getHeight() == target.getHeight()) {
                target.setWidth(target.getWidth() + origin.getWidth());
                target.setX(origin.getX());
                if (origin.getTextPositions().size() > 0) {
                    target.setTextPositions(origin.getTextPositions());
                }
                puzzlePieces.remove(origin);
                return true;
            }
        }
        return false;
    }

    private boolean mergeTopIfPossible(PuzzlePiece origin, PuzzlePiece target, List<PuzzlePiece> puzzlePieces) {
        if (checkMerge(origin, target)) {
            if (origin.getWidth() == target.getWidth()) {
                target.setHeight(target.getHeight() + origin.getHeight());
                puzzlePieces.remove(origin);
                return true;
            }
        }
        return false;
    }

    private boolean mergeBottomIfPossible(PuzzlePiece origin, PuzzlePiece target, List<PuzzlePiece> puzzlePieces) {
        if (checkMerge(origin, target)) {
            if (origin.getWidth() == target.getWidth()) {
                target.setHeight(target.getHeight() + origin.getHeight());
                target.setY(origin.getY());
                puzzlePieces.remove(origin);
                return true;
            }
        }
        return false;
    }

    protected List<PuzzlePiece> mergePuzzlePieces(List<PuzzlePiece> puzzlePieces) {
        boolean atLeastOneMerge = true;

        while (atLeastOneMerge) {
            atLeastOneMerge = false;

            for (int i = 0; i < puzzlePieces.size(); i++) {
                if (atLeastOneMerge) {
                    continue;
                }
                PuzzlePiece puzzlePiece = puzzlePieces.get(i);

                if (puzzlePiece.getX() == 0) {
                    PuzzlePiece puzzlePieceRight = getRightPuzzlePiece(puzzlePiece, puzzlePieces);
                    if (puzzlePieceRight != null) {
                        if (mergeRightIfPossible(puzzlePiece, puzzlePieceRight, puzzlePieces)) {
                            atLeastOneMerge = true;
                            continue;
                        }
                    }
                }

                PuzzlePiece puzzlePieceLeft = getLeftPuzzlePiece(puzzlePiece, puzzlePieces);
                if (puzzlePieceLeft != null) {
                    if (mergeLeftIfPossible(puzzlePiece, puzzlePieceLeft, puzzlePieces)) {
                        atLeastOneMerge = true;
                        continue;
                    }
                }

                if (puzzlePiece.getY() == 0) {
                    PuzzlePiece puzzlePieceBottom = getBottomPuzzlePiece(puzzlePiece, puzzlePieces);
                    if (puzzlePieceBottom != null) {
                        if (mergeTopIfPossible(puzzlePiece, puzzlePieceBottom, puzzlePieces)) {
                            atLeastOneMerge = true;
                        }
                    }
                }

                PuzzlePiece puzzlePieceTop = getBottomPuzzlePiece(puzzlePiece, puzzlePieces);
                if (puzzlePieceTop != null) {
                    if (mergeBottomIfPossible(puzzlePiece, puzzlePieceTop, puzzlePieces)) {
                        atLeastOneMerge = true;
                    }
                }
            }
        }

        return puzzlePieces;
    }

    protected List<PuzzlePiece> createPuzzle(List<TextPosition> textPositions, int sliceX, int sliceY) {
        double minX = textPositions.stream()
                .map(TextPosition::getX).min(Double::compareTo).orElse(0.0);
        double maxX = textPositions.stream()
                .map(position -> position.getX() + position.getWidth()).max(Double::compareTo).orElse(0.0);
        double minY = textPositions.stream()
                .map(TextPosition::getY).min(Double::compareTo).orElse(0.0);
        double maxY = textPositions.stream()
                .map(position -> position.getY() + position.getHeight()).max(Double::compareTo).orElse(0.0);
        double areaWidth = maxX - minX;
        double areaHeight = maxY - minY;
        double sliceWidth = areaWidth / sliceX;
        double sliceHeight = areaHeight / sliceY;

        List<PuzzlePiece> puzzlePieces = new ArrayList<>();

        for (int y = 0; y < sliceY; y++) {
            for (int x =0; x < sliceX; x++) {
                double puzzlePositionX = x * sliceWidth;
                double puzzlePositionY = y * sliceHeight;
                List<TextPosition> textUnderPuzzleArea = textPositions.stream()
                        .filter(textPosition -> textPosition.getX() >= puzzlePositionX && textPosition.getX() <= puzzlePositionX + sliceWidth)
                        .filter(textPosition -> textPosition.getY() >= puzzlePositionY && textPosition.getY() <= puzzlePositionY + sliceHeight)
                        .toList();

                String puzzleText = textUnderPuzzleArea.stream()
                        .reduce("", (s, textPosition) -> s + textPosition.getText(), String::concat);

                PuzzlePiece puzzlePiece = PuzzlePiece.builder()
                        .textPositions(textUnderPuzzleArea)
                        .hash(this.hash(puzzleText))
                        .x(x)
                        .y(y)
                        .width(sliceWidth)
                        .height(sliceHeight)
                        .build();
                puzzlePieces.add(puzzlePiece);
            }
        }

        return mergePuzzlePieces(puzzlePieces);
    }

    protected Boolean getBooleanParameter(String name) {
        for (Parameter parameter : this.parameters) {
            if (parameter.getName().equals(name) && parameter.getType() == ParameterType.BOOLEAN) {
                if (parameter.getValue() == null) {
                    return Boolean.parseBoolean(parameter.getDefaultValue());
                }
                return Boolean.parseBoolean(parameter.getValue());
            }
        }
        throw new IllegalArgumentException("Parameter " + name + " does not exist or is not a boolean");
    }

    protected Integer getIntegerParameter(String name) {
        for (Parameter parameter : this.parameters) {
            if (parameter.getName().equals(name) && parameter.getType() == ParameterType.INTEGER) {
                if (parameter.getValue() == null) {
                    return Integer.parseInt(parameter.getDefaultValue());
                }
                return Integer.parseInt(parameter.getValue());
            }
        }
        throw new IllegalArgumentException("Parameter " + name + " does not exist or is not an integer");
    }

    public void setParameter(String name, String value) {
        for (Parameter parameter : this.parameters) {
            if (parameter.getName().equals(name)) {
                parameter.setValue(value);
                return;
            }
        }
        throw new IllegalArgumentException("Parameter " + name + " does not exist");
    }

    public abstract HashResult compute(List<TextPosition> textPositions, String base64Image);
    public String hash(String text) {
        return DigestUtils.sha256Hex(text);
    }
}
