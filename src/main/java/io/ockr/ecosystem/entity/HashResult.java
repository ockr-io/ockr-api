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
    private List<String> subHashes;
    private String algorithm;
    private List<Parameter> parameters;

    @Override
    public String toString() {
        StringBuilder content = new StringBuilder(hash + "|");

        content.append(subHashes.size()).append("|");
        for (String subHash : subHashes) {
            content.append(subHash).append("|");
        }

        content.append(algorithm).append("|");

        for (Parameter parameter : parameters) {
            content.append(parameter.getName()).append("|");
            String value = parameter.getValue();
            if (value == null) {
                value = parameter.getDefaultValue();
            }
            content.append(value).append("|");
        }

        return content.toString();
    }

    public static HashResult fromString(String text) {
        String[] parts = text.split("\\|");
        String hash = parts[0];

        int subHashCount = Integer.parseInt(parts[1]);
        List<String> subHashes = List.of(parts).subList(2, 2 + subHashCount);

        String algorithm = parts[2 + subHashCount];

        List<String> parameterText = List.of(parts).subList(3 + subHashCount, parts.length);
        List<Parameter> parameters = new ArrayList<>();

        for (int i = 0; i < parameterText.size(); i += 2) {
            String parameterName = parameterText.get(i);
            String parameterValue = parameterText.get(i + 1);
            parameters.add(Parameter.builder()
                    .name(parameterName)
                    .value(parameterValue)
                    .build());
        }

        return HashResult.builder()
                .hash(hash)
                .subHashes(subHashes)
                .algorithm(algorithm)
                .parameters(parameters)
                .build();
    }
}
