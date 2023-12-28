package io.ockr.ecosystem.entity;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Puzzle {
    @Builder.Default
    List<PuzzlePiece> puzzlePieces = new ArrayList<>();
    double itemWidth;
    double itemHeight;
    double gridWidth;
    double gridHeight;

    public double getError() {
        return puzzlePieces.stream().map(PuzzlePiece::getError).reduce(0.0, Double::sum);
    }
}
