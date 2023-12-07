package io.ockr.ecosystem.entity;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class TextPosition {
    private Integer page;
    private String text;
    private Double x;
    private Double y;
    private Double width;
    private Double height;
}
