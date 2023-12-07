package io.ockr.ecosystem.algorithm;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Parameter {
    private String name;
    private ParameterType type;
    private String description;
    private String defaultValue;
    private String value;
}
