package io.ockr.ecosystem.algorithm;

import io.ockr.ecosystem.entity.PuzzlePiece;
import io.ockr.ecosystem.entity.TextPosition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

import java.util.List;

@SpringBootTest
@ComponentScan
public class PuzzlePingPongAlgorithmTest {

    @Test
    public void errorTest() {
        PuzzlePingPongAlgorithm puzzlePingPongAlgorithm = new PuzzlePingPongAlgorithm();

        List<TextPosition> textPositions = List.of(
                TextPosition.builder()
                        .x(0.0)
                        .y(0.0)
                        .width(100.0)
                        .height(100.0)
                        .text("Hellu World")
                        .page(0)
                        .build(),
                TextPosition.builder()
                        .x(20.0)
                        .y(20.0)
                        .width(100.0)
                        .height(100.0)
                        .text("I hope you r doing well")
                        .page(0)
                        .build()
        );

        List<TextPosition> groundTruth = List.of(
                TextPosition.builder()
                        .x(0.0)
                        .y(0.0)
                        .width(100.0)
                        .height(100.0)
                        .text("Hello World")
                        .page(0)
                        .build(),
                TextPosition.builder()
                        .x(18.0)
                        .y(18.0)
                        .width(100.0)
                        .height(100.0)
                        .text("I hope you are doing well")
                        .page(0)
                        .build()
        );


        double error = puzzlePingPongAlgorithm.error(textPositions, groundTruth);
        Assertions.assertEquals(11, textPositions.get(0).getText().length());
        Assertions.assertEquals(3.9899999999999993, error);
    }

    @Test
    public void provideHelp() {
        List<TextPosition> textPositions = List.of(
                TextPosition.builder()
                        .x(0.0)
                        .y(0.0)
                        .width(100.0)
                        .height(100.0)
                        .text("Hello World")
                        .page(0)
                        .build(),
                TextPosition.builder()
                        .x(20.0)
                        .y(120.0)
                        .width(150.0)
                        .height(100.0)
                        .text("I hope you are doing well")
                        .page(0)
                        .build(),
                TextPosition.builder()
                        .x(120.0)
                        .y(240.0)
                        .width(200.0)
                        .height(100.0)
                        .text("In a perfect world no one would need no help")
                        .page(0)
                        .build()
        );

        PuzzlePingPongAlgorithm puzzlePingPongAlgorithm = new PuzzlePingPongAlgorithm();
        List<PuzzlePiece> puzzlePieces = puzzlePingPongAlgorithm.createPuzzle(textPositions, 3, 3);

        Assertions.assertEquals(3, puzzlePieces.size());
        Assertions.assertEquals(320, puzzlePieces.get(0).getWidth());
        Assertions.assertEquals(340.0 / 3, puzzlePieces.get(0).getHeight());
        Assertions.assertEquals(1, puzzlePieces.get(0).getTextPositions().size());
        Assertions.assertEquals("Hello World", puzzlePieces.get(0).getTextPositions().get(0).getText());

        Assertions.assertEquals(320, puzzlePieces.get(1).getWidth());
        Assertions.assertEquals(340.0 / 3, puzzlePieces.get(1).getHeight());
        Assertions.assertEquals(1, puzzlePieces.get(1).getTextPositions().size());
        Assertions.assertEquals("I hope you are doing well", puzzlePieces.get(1).getTextPositions().get(0).getText());

        Assertions.assertEquals(320, puzzlePieces.get(2).getWidth());
        Assertions.assertEquals(340.0 / 3, puzzlePieces.get(2).getHeight());
        Assertions.assertEquals(1, puzzlePieces.get(2).getTextPositions().size());
        Assertions.assertEquals("In a perfect world no one would need no help", puzzlePieces.get(2).getTextPositions().get(0).getText());

        puzzlePieces.get(0).setTextPositions(List.of(TextPosition.builder()
                        .x(0.0)
                        .y(0.0)
                        .width(100.0)
                        .height(100.0)
                        .text("Hell0 World")
                        .page(0)
                        .build()));
        puzzlePieces.get(0).setError(1.0);

        puzzlePingPongAlgorithm.provideHelp(puzzlePieces, textPositions);

        Assertions.assertNotEquals(null, puzzlePieces.get(0).getHelper());
        Assertions.assertEquals(1, puzzlePieces.get(0).getHelper().size());
        Assertions.assertEquals("0", puzzlePieces.get(0).getHelper().get(0).getText());
        Assertions.assertEquals("o", puzzlePieces.get(0).getHelper().get(0).getPrediction());
        Assertions.assertEquals(4, puzzlePieces.get(0).getHelper().get(0).getStart());
        Assertions.assertEquals(4, puzzlePieces.get(0).getHelper().get(0).getEnd());
    }

}
