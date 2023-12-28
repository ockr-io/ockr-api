package io.ockr.ecosystem.entity.api;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.ockr.ecosystem.entity.TextPosition;
import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class InferenceResponse {
    private String ocrModelName;
    private String ocrModelVersion;
    private List<TextPosition> prediction;
    private Map<String, Object> parameters;
}
