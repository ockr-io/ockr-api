package io.ockr.ecosystem.entity.api;

import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InferenceResponse {
    private String model;
    private String hash;
    private List<String> subHashes;
    private String algorithm;
    private Map<String, String> algorithmParameters;
}
