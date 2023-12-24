package io.ockr.ecosystem.entity;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class TextPositionHelper {
    String text;
    String prediction;
    Integer start;
    Integer end;

    @Override
    public String toString() {
        return "[%s<>%s]@%d,%d".formatted(text, prediction, start, end);
    }
}
