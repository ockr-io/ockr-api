package io.ockr.ecosystem.algorithm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import io.ockr.ecosystem.common.Utils;
import io.ockr.ecosystem.entity.*;
import io.ockr.ecosystem.entity.api.InferenceResponse;
import io.ockr.ecosystem.service.ModelService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@SpringBootTest
@ComponentScan
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PuzzlePingPongAlgorithmTest {

    @Autowired
    ModelService modelService;

    private WireMockServer wireMockServer;

    @BeforeAll
    public void setup() throws JsonProcessingException {
        modelService.deleteAllModels();
        wireMockServer = new WireMockServer(6565);
        wireMockServer.start();

        modelService.saveModel(Model.builder()
                        .name("PP-OCRv3")
                        .port(6565)
                        .url("http://localhost")
                        .build());

        InferenceResponse inferenceResponse = InferenceResponse.builder()
                .ocrModelName("PP-OCRv3")
                .ocrModelVersion("latest")
                .parameters(Map.of(
                        "segmentation_threshold", 0.3,
                        "detection_threshold", 0.6,
                        "unclip_ratio", 3,
                        "max_candidates", 1000,
                        "min_size", 3
                ))
                .prediction(List.of(
                        TextPosition.builder()
                                .x(30.0)
                                .y(60.0)
                                .width(174.0)
                                .height(30.0)
                                .text("Hello World")
                                .page(0)
                                .build(),
                        TextPosition.builder()
                                .x(137.0)
                                .y(295.0)
                                .width(226.0)
                                .height(42.0)
                                .text("How are you")
                                .page(0)
                                .build(),
                        TextPosition.builder()
                                .x(30.0)
                                .y(374.0)
                                .width(20.0)
                                .height(30.0)
                                .text("1")
                                .page(0)
                                .build()
                ))
                .build();

        ObjectMapper mapper = new ObjectMapper();

        wireMockServer.stubFor(post(urlEqualTo("/inference"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(mapper.writeValueAsString(inferenceResponse))));
    }

    @AfterAll
    public void teardown() {
        modelService.deleteAllModels();
        wireMockServer.stop();
    }

    @Test
    public void errorTest() {
        PuzzlePingPongAlgorithm puzzlePingPongAlgorithm = new PuzzlePingPongAlgorithm(modelService);

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

        PuzzlePingPongAlgorithm puzzlePingPongAlgorithm = new PuzzlePingPongAlgorithm(modelService);
        Puzzle puzzle = puzzlePingPongAlgorithm.createPuzzle(textPositions, 3, 3);
        List<PuzzlePiece> puzzlePieces = puzzle.getPuzzlePieces();

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

        Assertions.assertEquals(1, puzzlePieces.get(0).getHelper().size());
        Assertions.assertEquals("0", puzzlePieces.get(0).getHelper().get(0).getText());
        Assertions.assertEquals("o", puzzlePieces.get(0).getHelper().get(0).getPrediction());
        Assertions.assertEquals(4, puzzlePieces.get(0).getHelper().get(0).getStart());
        Assertions.assertEquals(4, puzzlePieces.get(0).getHelper().get(0).getEnd());

        puzzlePieces.get(0).setTextPositions(List.of(TextPosition.builder()
                .x(0.0)
                .y(0.0)
                .width(100.0)
                .height(100.0)
                .text("Hello Worlg")
                .page(0)
                .build()));
        puzzlePieces.get(0).setHelper(new ArrayList<>());

        puzzlePingPongAlgorithm.provideHelp(puzzlePieces, textPositions);

        Assertions.assertEquals(1, puzzlePieces.get(0).getHelper().size());
        Assertions.assertEquals("g", puzzlePieces.get(0).getHelper().get(0).getText());
        Assertions.assertEquals("d", puzzlePieces.get(0).getHelper().get(0).getPrediction());
        Assertions.assertEquals(10, puzzlePieces.get(0).getHelper().get(0).getStart());
        Assertions.assertEquals(10, puzzlePieces.get(0).getHelper().get(0).getEnd());

        puzzlePieces.get(0).setTextPositions(List.of(TextPosition.builder()
                .x(0.0)
                .y(0.0)
                .width(100.0)
                .height(100.0)
                .text("Holl0 worrg")
                .page(0)
                .build()));
        puzzlePieces.get(0).setHelper(new ArrayList<>());

        puzzlePingPongAlgorithm.provideHelp(puzzlePieces, textPositions);

        Assertions.assertEquals(1, puzzlePieces.get(0).getHelper().size());
        Assertions.assertEquals("Holl0 worrg", puzzlePieces.get(0).getHelper().get(0).getText());
        Assertions.assertEquals("Hello World", puzzlePieces.get(0).getHelper().get(0).getPrediction());
        Assertions.assertEquals(0, puzzlePieces.get(0).getHelper().get(0).getStart());
        Assertions.assertEquals(10, puzzlePieces.get(0).getHelper().get(0).getEnd());

        puzzlePieces.get(0).setTextPositions(List.of(TextPosition.builder()
                .x(0.0)
                .y(0.0)
                .width(100.0)
                .height(100.0)
                .text("Hello Worrg")
                .page(0)
                .build()));
        puzzlePieces.get(0).setHelper(new ArrayList<>());

        puzzlePingPongAlgorithm.provideHelp(puzzlePieces, textPositions);

        Assertions.assertEquals(1, puzzlePieces.get(0).getHelper().size());
        Assertions.assertEquals("rg", puzzlePieces.get(0).getHelper().get(0).getText());
        Assertions.assertEquals("ld", puzzlePieces.get(0).getHelper().get(0).getPrediction());
        Assertions.assertEquals(9, puzzlePieces.get(0).getHelper().get(0).getStart());
        Assertions.assertEquals(10, puzzlePieces.get(0).getHelper().get(0).getEnd());

        puzzlePieces.get(0).setTextPositions(List.of(TextPosition.builder()
                .x(0.0)
                .y(0.0)
                .width(100.0)
                .height(100.0)
                .text("Hello Wrrrd")
                .page(0)
                .build()));
        puzzlePieces.get(0).setHelper(new ArrayList<>());

        puzzlePingPongAlgorithm.provideHelp(puzzlePieces, textPositions);

        Assertions.assertEquals(2, puzzlePieces.get(0).getHelper().size());
        Assertions.assertEquals("r", puzzlePieces.get(0).getHelper().get(0).getText());
        Assertions.assertEquals("o", puzzlePieces.get(0).getHelper().get(0).getPrediction());
        Assertions.assertEquals(7, puzzlePieces.get(0).getHelper().get(0).getStart());
        Assertions.assertEquals(7, puzzlePieces.get(0).getHelper().get(0).getEnd());

        Assertions.assertEquals("r", puzzlePieces.get(0).getHelper().get(1).getText());
        Assertions.assertEquals("l", puzzlePieces.get(0).getHelper().get(1).getPrediction());
        Assertions.assertEquals(9, puzzlePieces.get(0).getHelper().get(1).getStart());
        Assertions.assertEquals(9, puzzlePieces.get(0).getHelper().get(1).getEnd());
    }

    @Test
    public void calculatePuzzleErrorTest() {
        List<TextPosition> prediction = List.of(
                TextPosition.builder()
                        .x(1.0)
                        .y(1.0)
                        .width(110.0)
                        .height(100.0)
                        .text("Hello World")
                        .page(0)
                        .build(),
                TextPosition.builder()
                        .x(25.0)
                        .y(115.0)
                        .width(155.0)
                        .height(110.0)
                        .text("I hope you are doing weII")
                        .page(0)
                        .build(),
                TextPosition.builder()
                        .x(120.0)
                        .y(240.0)
                        .width(200.0)
                        .height(100.0)
                        .text("In a perfect w0rld n0 0ne would need no help")
                        .page(0)
                        .build()
        );

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

        PuzzlePingPongAlgorithm puzzlePingPongAlgorithm = new PuzzlePingPongAlgorithm(modelService);
        Puzzle puzzle = puzzlePingPongAlgorithm.createPuzzle(textPositions, 3, 3);
        puzzlePingPongAlgorithm.calculatePuzzleError(puzzle, prediction);
        List<PuzzlePiece> puzzlePieces = puzzle.getPuzzlePieces();

        Assertions.assertEquals(1.1989999999999998, puzzlePieces.get(0).getError());
        Assertions.assertEquals(5.739002932551319, puzzlePieces.get(1).getError());
        Assertions.assertEquals(3.0, puzzlePieces.get(2).getError());
    }

    @Test
    public void example1PuzzleTest() {
        List<TextPosition> textPositions = List.of(
                TextPosition.builder()
                        .x(30.0)
                        .y(60.0)
                        .width(178.0)
                        .height(32.0)
                        .text("Hello World")
                        .page(0)
                        .build(),
                TextPosition.builder()
                        .x(138.0)
                        .y(296.0)
                        .width(226.0)
                        .height(42.0)
                        .text("How are you")
                        .page(0)
                        .build(),
                TextPosition.builder()
                        .x(32.0)
                        .y(374.0)
                        .width(22.0)
                        .height(32.0)
                        .text("1")
                        .page(0)
                        .build()
        );
        PuzzlePingPongAlgorithm puzzlePingPongAlgorithm = new PuzzlePingPongAlgorithm(modelService);
        Puzzle puzzle = puzzlePingPongAlgorithm.createPuzzle(textPositions, 3, 3);
        Assertions.assertEquals(334, puzzle.getGridWidth());
        Assertions.assertEquals(346, puzzle.getGridHeight());
        Assertions.assertEquals(111.33333333333333, puzzle.getItemWidth());
        Assertions.assertEquals(115.33333333333333, puzzle.getItemHeight());
        Assertions.assertEquals(2, puzzle.getPuzzlePieces().size());
        Assertions.assertEquals(1, puzzle.getPuzzlePieces().get(0).getTextPositions().size());
        Assertions.assertEquals("Hello World", puzzle.getPuzzlePieces().get(0).getTextPositions().get(0).getText());
        Assertions.assertEquals(2, puzzle.getPuzzlePieces().get(1).getTextPositions().size());
        Assertions.assertEquals("How are you", puzzle.getPuzzlePieces().get(1).getTextPositions().get(0).getText());
        Assertions.assertEquals("1", puzzle.getPuzzlePieces().get(1).getTextPositions().get(1).getText());
    }

    @Test
    public void calculateTest() throws IOException {
        String base64Image = Utils.loadFileContent("text/example_1.txt");
        PuzzlePingPongAlgorithm puzzlePingPongAlgorithm = new PuzzlePingPongAlgorithm(modelService);
        List<TextPosition> textPositions = List.of(
                TextPosition.builder()
                        .x(30.0)
                        .y(60.0)
                        .width(178.0)
                        .height(32.0)
                        .text("Hello World")
                        .page(0)
                        .build(),
                TextPosition.builder()
                        .x(138.0)
                        .y(296.0)
                        .width(226.0)
                        .height(42.0)
                        .text("How are you")
                        .page(0)
                        .build(),
                TextPosition.builder()
                        .x(32.0)
                        .y(374.0)
                        .width(22.0)
                        .height(32.0)
                        .text("1")
                        .page(0)
                        .build()
        );
        HashResult hashResult = puzzlePingPongAlgorithm.compute(textPositions, base64Image);
        Assertions.assertEquals(3, hashResult.getPuzzlePieces().size());

        Assertions.assertEquals(1, hashResult.getPuzzlePieces().get(0).getTextPositions().size());
        Assertions.assertEquals("Hello World", hashResult.getPuzzlePieces().get(0).getTextPositions().get(0).getText());
        Assertions.assertEquals(1, hashResult.getPuzzlePieces().get(1).getTextPositions().size());
        Assertions.assertEquals("How are you", hashResult.getPuzzlePieces().get(1).getTextPositions().get(0).getText());
        Assertions.assertEquals(1, hashResult.getPuzzlePieces().get(2).getTextPositions().size());
        Assertions.assertEquals("1", hashResult.getPuzzlePieces().get(2).getTextPositions().get(0).getText());

        Assertions.assertEquals(334, hashResult.getGridWidth());
        Assertions.assertEquals(346, hashResult.getGridHeight());

        Assertions.assertEquals(83.5, hashResult.getPuzzleWidth());
        Assertions.assertEquals(43.25, hashResult.getPuzzleHeight());

        Assertions.assertEquals(0, hashResult.getPuzzlePieces().get(0).getX());
        Assertions.assertEquals(0, hashResult.getPuzzlePieces().get(0).getY());
        Assertions.assertEquals(334, hashResult.getPuzzlePieces().get(0).getWidth());
        Assertions.assertEquals(43.25, hashResult.getPuzzlePieces().get(0).getHeight());

        Assertions.assertEquals(0, hashResult.getPuzzlePieces().get(1).getX());
        Assertions.assertEquals(1, hashResult.getPuzzlePieces().get(1).getY());
        Assertions.assertEquals(334, hashResult.getPuzzlePieces().get(1).getWidth());
        Assertions.assertEquals(216.25, hashResult.getPuzzlePieces().get(1).getHeight());

        Assertions.assertEquals(0, hashResult.getPuzzlePieces().get(2).getX());
        Assertions.assertEquals(6, hashResult.getPuzzlePieces().get(2).getY());
        Assertions.assertEquals(334, hashResult.getPuzzlePieces().get(2).getWidth());
        Assertions.assertEquals(86.5, hashResult.getPuzzlePieces().get(2).getHeight());

        double totalPuzzleHeight = hashResult.getPuzzlePieces().get(0).getHeight()
                + hashResult.getPuzzlePieces().get(1).getHeight()
                + hashResult.getPuzzlePieces().get(2).getHeight();
        Assertions.assertEquals(hashResult.getGridHeight(), totalPuzzleHeight);
    }
}
