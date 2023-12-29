package io.ockr.ecosystem.entity;

import io.ockr.ecosystem.algorithm.Parameter;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HashResult {
    private String hash;
    private String algorithm;
    private List<Parameter> parameters;

    private List<PuzzlePiece> puzzlePieces;

    private double gridWidth;
    private double gridHeight;
    private double puzzleWidth;
    private double puzzleHeight;

    @Override
    public String toString() {
        StringBuilder content = new StringBuilder(hash + "|");
        content.append(algorithm).append("|");
        content.append(gridWidth).append("|");
        content.append(gridHeight).append("|");
        content.append(puzzleWidth).append("|");
        content.append(puzzleHeight).append("|");

        for (Parameter parameter : parameters) {
            content.append(parameter.getName()).append("$");
            String value = parameter.getValue();
            if (value == null) {
                value = parameter.getDefaultValue();
            }
            content.append(value).append("$");
        }

        content.append("|");

        for (PuzzlePiece puzzlePiece : puzzlePieces) {
            content.append(puzzlePiece.toString()).append("&");
        }

        return content.toString();
    }

    public static HashResult fromString(String text) {
        String[] parts = text.split("\\|");
        String hash = parts[0];
        String algorithm = parts[1];
        double gridWidth = Double.parseDouble(parts[2]);
        double gridHeight = Double.parseDouble(parts[3]);
        double puzzleWidth = Double.parseDouble(parts[4]);
        double puzzleHeight = Double.parseDouble(parts[5]);
        String[] parameters = parts[6].split("\\$");
        List<Parameter> parameterList = new ArrayList<>();

        for (int i = 0; i < parameters.length; i += 2) {
            parameterList.add(Parameter.builder()
                    .name(parameters[i])
                    .value(parameters[i + 1])
                    .build());
        }

        String[] puzzlePieces = parts[7].split("&");
        List<PuzzlePiece> puzzlePieceList = new ArrayList<>();

        for (String puzzlePiece : puzzlePieces) {
            puzzlePieceList.add(PuzzlePiece.fromString(puzzlePiece));
        }

        return HashResult.builder()
                .hash(hash)
                .algorithm(algorithm)
                .parameters(parameterList)
                .puzzlePieces(puzzlePieceList)
                .gridWidth(gridWidth)
                .gridHeight(gridHeight)
                .puzzleWidth(puzzleWidth)
                .puzzleHeight(puzzleHeight)
                .build();
    }
}
