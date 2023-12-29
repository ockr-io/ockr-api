package io.ockr.ecosystem.algorithm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.ockr.ecosystem.entity.*;
import io.ockr.ecosystem.entity.api.InferenceResponse;
import io.ockr.ecosystem.service.ModelService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The PuzzlePingPongAlgorithm ensures data correctness by testing the OCR while creating the QR code.
 * It adds parameters, model name and version, as well as helper to the QR code.
 */
public class PuzzlePingPongAlgorithm extends Algorithm {

    private final ModelService modelService;

    public PuzzlePingPongAlgorithm(ModelService modelService) {
        super("puzzle-ping-pong");
        this.modelService = modelService;
        Parameter maxPuzzlesX = Parameter.builder()
                .name("maxPuzzlesX")
                .type(ParameterType.INTEGER)
                .description("The maximum number of puzzles to use in x direction")
                .defaultValue("4")
                .build();
        Parameter maxPuzzlesY = Parameter.builder()
                .name("maxPuzzlesY")
                .type(ParameterType.INTEGER)
                .description("The maximum number of puzzles to use in y direction")
                .defaultValue("8")
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

        this.parameters.addAll(List.of(maxPuzzlesX, maxPuzzlesY, modelName, modelVersion, maxIterations, provideHelp, initialModelParameters));
    }

    private InferenceResponse ping(Model model, String base64Image, String modelVersion, Map<String, Object> parameters) {
        try {
            return modelService.inference(model, base64Image, modelVersion, parameters);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private double overlap(TextPosition a, TextPosition b) {
        double overlapX = Math.min(a.getX() + a.getWidth(), b.getX() + b.getWidth()) - Math.max(a.getX(), b.getX());
        double overlapY = Math.min(a.getY() + a.getHeight(), b.getY() + b.getHeight()) - Math.max(a.getY(), b.getY());
        return overlapX * overlapY;
    }

    public double relativeOverlap(TextPosition a, TextPosition b) {
        return overlap(a, b) / area(a);
    }

    private int levenshteinDistance(String a, String b) {
        int aLength = a.length() + 1;
        int bLength = b.length() + 1;

        int[] cost = new int[aLength];
        int[] update = new int[aLength];

        for (int i = 0; i < aLength; i++) cost[i] = i;

        for (int j = 1; j < bLength; j++) {
            update[0] = j;

            for(int i = 1; i < aLength; i++) {
                int match = (a.charAt(i - 1) == b.charAt(j - 1)) ? 0 : 1;
                int costReplace = cost[i - 1] + match;
                int costInsert  = cost[i] + 1;
                int costDelete  = update[i - 1] + 1;
                update[i] = Math.min(Math.min(costInsert, costDelete), costReplace);
            }
            int[] swap = cost; cost = update; update = swap;
        }
        return cost[aLength - 1];
    }

    protected double error(List<TextPosition> inferenceResult, List<TextPosition> groundTruth) {
        // calculate the error between the inference response and the ground truth
        double error = 0;
        for (TextPosition textPosition : groundTruth) {
            TextPosition match = findMatch(textPosition, inferenceResult);

            if (match == null) {
                // Get full bounding box error if no match is found
                error += area(textPosition);
                // Receive a penalty for each character that is not found
                error += textPosition.getText().length();
            } else {
                int textLength = textPosition.getText().length();
                double relativeOverlap = relativeOverlap(match, textPosition);
                error += (1 - relativeOverlap) * textLength;
                error += levenshteinDistance(match.getText(), textPosition.getText());
            }
        }

        return error;
    }


    private double area(TextPosition textPosition) {
        return textPosition.getWidth() * textPosition.getHeight();
    }

    @Override
    public HashResult compute(List<TextPosition> textPositions, String base64Image) {
        Model model = modelService.getModelByName(this.getStringParameter("modelName"));

        if (model == null) {
            throw new IllegalArgumentException("Model does not exist. Or is not registered.");
        }

        String modelVersion = this.getStringParameter("modelVersion");
        String initialModelParameters = this.getStringParameter("initialModelParameters");

        Map<String, Object> parameters = null;
        if (initialModelParameters != null && !initialModelParameters.equals("{}")) {
            try {
                parameters = new ObjectMapper().readValue(initialModelParameters, new TypeReference<>() {});
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        int maxIterations = this.getIntegerParameter("maxIterations");
        int maxPuzzlesX = this.getIntegerParameter("maxPuzzlesX");
        int maxPuzzlesY = this.getIntegerParameter("maxPuzzlesY");
        boolean provideHelp = this.getBooleanParameter("provideHelp");

        InferenceResponse inferenceResponse = this.ping(model, base64Image, modelVersion, parameters);

        Puzzle puzzle = this.createPuzzle(textPositions, maxPuzzlesX, maxPuzzlesY);
        calculatePuzzleError(puzzle, inferenceResponse.getPrediction());

        double error = puzzle.getError();
        parameters = inferenceResponse.getParameters();

        int iteration = 0;
        while (iteration < maxIterations && error > 0) {
            Map<String, Object> newParameters = this.pong(puzzle.getPuzzlePieces(), parameters);
            inferenceResponse = this.ping(model, base64Image, modelVersion, newParameters);
            calculatePuzzleError(puzzle, inferenceResponse.getPrediction());
            double newError = puzzle.getError();

            if (newError < error) {
                error = newError;
                parameters = newParameters;
            }

            iteration++;
        }

        if (error > 0 && provideHelp) {
            this.provideHelp(puzzle.getPuzzlePieces(), inferenceResponse.getPrediction());
        }

        HashResult result = new HashResult();
        result.setAlgorithm(this.getName());
        try {
            this.setParameter("initialModelParameters", new ObjectMapper().writeValueAsString(parameters));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        result.setParameters(this.getParameters());
        String text = textPositions.stream()
                .reduce("", (s, textPosition) -> s + textPosition.getText(), String::concat);
        result.setHash(this.hash(text));
        result.setPuzzlePieces(puzzle.getPuzzlePieces());

        result.setGridWidth(puzzle.getGridWidth());
        result.setGridHeight(puzzle.getGridHeight());
        result.setPuzzleWidth(puzzle.getItemWidth());
        result.setPuzzleHeight(puzzle.getItemHeight());

        if (result.toString().length() > MAX_QR_CODE_CHARS) {
            throw new RuntimeException("The QR code content is too big. The limit is " +
                    MAX_QR_CODE_CHARS + " characters and the current content has " +
                    result.toString().length() + " characters.");
        }

        return result;
    }

    private TextPosition findMatch(TextPosition textPosition, List<TextPosition> prediction) {
        // try to find matches near the bounding box position
        double tolerance = 64;
        List<TextPosition> matches = prediction.stream()
                .filter(result -> result.getX() >= textPosition.getX() - tolerance && result.getX() <= textPosition.getX() + tolerance)
                .filter(result -> result.getY() >= textPosition.getY() - tolerance && result.getY() <= textPosition.getY() + tolerance)
                .toList();

        TextPosition match = null;
        if (matches.size() > 1) {
            // find the best match by calculating the distance between the bounding box
            double overlap = 0;
            TextPosition possibleMatch = null;
            for (TextPosition option : matches) {
                double optionOverlap = overlap(option, textPosition);
                if (optionOverlap > overlap) {
                    overlap = optionOverlap;
                    possibleMatch = option;
                }
            }
            match = possibleMatch;
        } else if (matches.size() == 1) {
            // check if the match is within the bounding box
            double overlap = overlap(matches.get(0), textPosition);
            if (overlap > 0) {
                match = matches.get(0);
            }
        }
        return match;
    }

    protected void provideHelp(List<PuzzlePiece> puzzlePieces, List<TextPosition> prediction) {
        for (PuzzlePiece puzzlePiece : puzzlePieces) {
            if (puzzlePiece.getError() > 0) {
                for (TextPosition textPosition : puzzlePiece.getTextPositions()) {
                    TextPosition match = findMatch(textPosition, prediction);
                    double distance = levenshteinDistance(textPosition.getText(), match.getText());
                    if (match != null && distance > 0) {
                        double distanceThreshold =  textPosition.getText().length() / 4.0;
                        if (distance > distanceThreshold) {
                                puzzlePiece.getHelper().add(TextPositionHelper.builder()
                                        .text(textPosition.getText())
                                        .prediction(match.getText())
                                        .start(0)
                                        .end(textPosition.getText().length() - 1)
                                        .build());
                        } else {
                            // find differences in the text
                            int start = 0;
                            int end = 0;
                            for (int i = 0; i < textPosition.getText().length(); i++) {
                                if (textPosition.getText().charAt(i) != match.getText().charAt(i)) {
                                    if (start == 0) {
                                        start = i;
                                    }
                                    end = i;

                                    if (i == textPosition.getText().length() - 1) {
                                        puzzlePiece.getHelper().add(TextPositionHelper.builder()
                                                .text(textPosition.getText().substring(start, end + 1))
                                                .prediction(match.getText().substring(start, end + 1))
                                                .start(start)
                                                .end(end)
                                                .build());
                                    }

                                } else {
                                    if (start != 0) {
                                        puzzlePiece.getHelper().add(TextPositionHelper.builder()
                                                .text(textPosition.getText().substring(start, end + 1))
                                                .prediction(match.getText().substring(start, end + 1))
                                                .start(start)
                                                .end(end)
                                                .build());

                                        start = 0;
                                        end = 0;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    protected void calculatePuzzleError(Puzzle puzzle, List<TextPosition> prediction) {
        List<PuzzlePiece> puzzlePieces = puzzle.getPuzzlePieces();
        for (PuzzlePiece puzzlePiece : puzzlePieces) {
            double puzzlePieceX = puzzlePiece.getX() * puzzle.getItemWidth();
            double puzzlePieceY = puzzlePiece.getY() * puzzle.getItemHeight();
            List<TextPosition> predictionUnderPuzzlePiece = prediction.stream()
                    .filter(textPosition -> textPosition.getX() >= puzzlePieceX &&
                            textPosition.getX() <= puzzlePieceX + puzzlePiece.getWidth() &&
                            textPosition.getY() >= puzzlePieceY &&
                            textPosition.getY() <= puzzlePieceY + puzzlePiece.getHeight())
                    .toList();

            List<TextPosition> puzzlePieceTextPositions = puzzlePiece.getTextPositions();
            List<Integer> matches = new ArrayList<>();

            // find matches and calculate error
            for (TextPosition textPosition : puzzlePieceTextPositions) {
                TextPosition match = findMatch(textPosition, predictionUnderPuzzlePiece);
                if (match == null) {
                    // Get full bounding box error if no match is found
                    puzzlePiece.setError(puzzlePiece.getError() + area(textPosition));
                } else {
                    double relativeOverlap = relativeOverlap(match, textPosition);
                    puzzlePiece.setError(puzzlePiece.getError() + (1 - relativeOverlap) * textPosition.getText().length());
                    puzzlePiece.setError(puzzlePiece.getError() + levenshteinDistance(match.getText(), textPosition.getText()));
                    matches.add(predictionUnderPuzzlePiece.indexOf(match));
                }
            }

            // add error for all remaining text positions
            for (int i= 0; i < predictionUnderPuzzlePiece.size(); i++) {
                if (!matches.contains(i)) {
                    puzzlePiece.setError(puzzlePiece.getError() + area(predictionUnderPuzzlePiece.get(i)));
                }
            }
        }
    }

    private Map<String, Object> pong(List<PuzzlePiece> puzzlePieces, Map<String, Object> parameters) {
        Map<String, Object> newParameters = new java.util.HashMap<>(Map.copyOf(parameters));
        List<String> parameterNames = parameters.keySet().stream().toList();

        if (parameterNames.size() > 0) {
            Object parameter = newParameters.get(parameterNames.get(0));
            if (parameter instanceof Integer) {
                int value = (int) parameter;
                value += Math.random() > 0.5 ? 1 : -1;
                newParameters.replace(parameterNames.get(0), value);
            } else if (parameter instanceof Double) {
                double value = (double) parameter;
                value += Math.random() > 0.5 ? 0.1 : -0.1;
                newParameters.replace(parameterNames.get(0), value);
            }
        }

        return newParameters;
    }
}
