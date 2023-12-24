package io.ockr.ecosystem.entity.api;

import io.ockr.ecosystem.entity.TextPosition;
import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InferenceResponse {
    private String ocrModelName;
    private String ocrModelVersion;
    private List<TextPosition> prediction;
    private Map<String, Object> parameters;
}
