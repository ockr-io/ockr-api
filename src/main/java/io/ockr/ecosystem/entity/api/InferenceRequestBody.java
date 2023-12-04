package io.ockr.ecosystem.entity.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class InferenceRequestBody {
    private String model;
    private String base64Image;
}