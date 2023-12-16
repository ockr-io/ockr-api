package io.ockr.ecosystem.algorithm;

import io.ockr.ecosystem.entity.HashResult;
import io.ockr.ecosystem.entity.TextPosition;

import java.util.List;

/**
 * The PuzzlePingPongAlgorithm ensures data correctness by testing the OCR while creating the QR code.
 * It adds parameters, model name and version, as well as helper to the QR code.
 */
public class PuzzlePingPongAlgorithm extends Algorithm {

    private final int MAX_QR_CODE_CHARS = 7089;

    public PuzzlePingPongAlgorithm() {
        super("puzzle-ping-pong");
        Parameter maxPuzzles = Parameter.builder()
                .name("maxPuzzles")
                .type(ParameterType.INTEGER)
                .description("The maximum number of puzzles to use")
                .defaultValue("9")
                .build();
        Parameter modelName = Parameter.builder()
                .name("modelName")
                .type(ParameterType.STRING)
                .description("The name of the model")
                .defaultValue("PP-OCRv3")
                .build();
        Parameter modelVersion = Parameter.builder()
                .name("modelVersion")
                .type(ParameterType.STRING)
                .description("The version of the model")
                .defaultValue("latest")
                .build();
        Parameter maxIterations = Parameter.builder()
                .name("maxIterations")
                .type(ParameterType.INTEGER)
                .description("The maximum number of iterations. Before help will be provided or the early stopping criteria is met")
                .defaultValue("10")
                .build();
        Parameter provideHelp = Parameter.builder()
                .name("provideHelp")
                .type(ParameterType.BOOLEAN)
                .description("Whether to include help or not to the QR code")
                .defaultValue("true")
                .build();
        Parameter initialModelParameters = Parameter.builder()
                .name("initialModelParameters")
                .type(ParameterType.STRING)
                .description("The initial model parameters as a JSON string")
                .defaultValue("{}")
                .build();

        this.parameters.addAll(List.of(maxPuzzles, modelName, modelVersion, maxIterations, provideHelp, initialModelParameters));
    }

    @Override
    public HashResult compute(List<TextPosition> textPositions, String base64Image) {
        return null;
    }
}
