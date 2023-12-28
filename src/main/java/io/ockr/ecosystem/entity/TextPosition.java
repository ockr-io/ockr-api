package io.ockr.ecosystem.entity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class TextPosition {
    private Integer page;
    private String text;
    private Double x;
    private Double y;
    private Double width;
    private Double height;

    @Override
    public boolean equals(Object textPosition) {
        return textPosition.getClass() == this.getClass() &&
                this.text.equals(((TextPosition) textPosition).getText()) &&
                this.x.equals(((TextPosition) textPosition).getX()) &&
                this.y.equals(((TextPosition) textPosition).getY()) &&
                this.width.equals(((TextPosition) textPosition).getWidth()) &&
                this.height.equals(((TextPosition) textPosition).getHeight());
    }
}
