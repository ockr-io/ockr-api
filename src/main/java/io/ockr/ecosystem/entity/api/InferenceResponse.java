package io.ockr.ecosystem.entity.api;

import io.ockr.ecosystem.entity.TextPosition;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InferenceResponse {
    private String model;
    private List<TextPosition> ocrResults;
}
