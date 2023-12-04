package io.ockr.ecosystem.entity.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ModelRequestBody {
    private String name;
    private String url;
    private Integer port;
}
