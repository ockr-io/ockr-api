package io.ockr.ecosystem.algorithm;

import io.ockr.ecosystem.entity.HashResult;
import io.ockr.ecosystem.entity.PuzzlePiece;
import io.ockr.ecosystem.entity.TextPosition;
import io.ockr.ecosystem.entity.api.InferenceResponse;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.ArrayList;
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
        return puzzlePieces.stream().filter(piece -> piece.getX() == puzzlePiece.getX() - 1 && piece.getY() == puzzlePiece.getY()).findFirst().orElse(null);
    }

    private PuzzlePiece getRightPuzzlePiece(PuzzlePiece puzzlePiece, List<PuzzlePiece> puzzlePieces) {
        return puzzlePieces.stream().filter(piece -> piece.getX() == puzzlePiece.getX() + 1 && piece.getY() == puzzlePiece.getY()).findFirst().orElse(null);
    }

    private PuzzlePiece getTopPuzzlePiece(PuzzlePiece puzzlePiece, List<PuzzlePiece> puzzlePieces) {
        return puzzlePieces.stream().filter(piece -> piece.getX() == puzzlePiece.getX() && piece.getY() == puzzlePiece.getY() - 1).findFirst().orElse(null);
    }

    private PuzzlePiece getBottomPuzzlePiece(PuzzlePiece puzzlePiece, List<PuzzlePiece> puzzlePieces) {
        return puzzlePieces.stream().filter(piece -> piece.getX() == puzzlePiece.getX() && piece.getY() == puzzlePiece.getY() + 1).findFirst().orElse(null);
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

                if (puzzlePiece.getY() == 0) {
                    PuzzlePiece puzzlePieceTop = getTopPuzzlePiece(puzzlePiece, puzzlePieces);
                    if (puzzlePieceTop != null) {
                        if (mergeTopIfPossible(puzzlePiece, puzzlePieceTop, puzzlePieces)) {
                            atLeastOneMerge = true;
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

                PuzzlePiece puzzlePieceBottom = getBottomPuzzlePiece(puzzlePiece, puzzlePieces);
                if (puzzlePieceBottom != null) {
                    if (mergeBottomIfPossible(puzzlePiece, puzzlePieceBottom, puzzlePieces)) {
                        atLeastOneMerge = true;
                    }
                }
            }
        }

        return puzzlePieces;
    }

    protected List<PuzzlePiece> createPuzzle(List<TextPosition> textPositions, int sliceX, int sliceY) {
        double minX = textPositions.stream().mapToDouble(TextPosition::getX).min().orElse(0);
        double maxX = textPositions.stream().mapToDouble(TextPosition::getX).max().orElse(0);
        double minY = textPositions.stream().mapToDouble(TextPosition::getY).min().orElse(0);
        double maxY = textPositions.stream().mapToDouble(TextPosition::getY).max().orElse(0);
        double areaWidth = maxX - minX;
        double areaHeight = maxY - minY;
        double sliceWidth = areaWidth / sliceX;
        double sliceHeight = areaHeight / sliceY;

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

                PuzzlePiece puzzlePiece = PuzzlePiece.builder()
                        .textPositions(textUnderPuzzleArea)
                        .hash(this.hash(puzzleText))
                        .x(i)
                        .y(j)
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
