package io.ockr.ecosystem.algorithm;

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

}
