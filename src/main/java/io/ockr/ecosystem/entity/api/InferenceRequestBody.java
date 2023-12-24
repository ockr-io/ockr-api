package io.ockr.ecosystem.entity.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class InferenceRequestBody {
    private String modelName;
    private String modelVersion;
    private String base64Image;
    private Map<String, Object> parameters;
}