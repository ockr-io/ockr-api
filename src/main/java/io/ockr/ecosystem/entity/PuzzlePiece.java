package io.ockr.ecosystem.entity;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class PuzzlePiece {
    private List<TextPosition> textPositions;
    private String hash;
    private double error;
    private int x;
    private int y;
    private double width;
    private double height;

    private List<TextPositionHelper> helper;

    private static String formatDouble(double number) {
        return String.format("%.6f", number)
                .replaceAll("0*$", "")
                .replaceAll("\\.$", "")
                .replaceAll(",", ".");
    }

    public static PuzzlePiece fromString(String text) {
        String[] parts = text.split("#");
        String hash = parts[0];
        int x = Integer.parseInt(parts[1]);
        int y = Integer.parseInt(parts[2]);
        double width = Double.parseDouble(parts[3]);
        double height = Double.parseDouble(parts[4]);

        List<TextPositionHelper> helper = new ArrayList<>();
        if (parts.length == 6) {
            String[] textPositionHelpers = parts[5].split("\\$");
            for (String textPositionHelper : textPositionHelpers) {
                String[] textPositionHelperParts = textPositionHelper.split("<>");
                String[] textPositionHelperParts2 = textPositionHelperParts[1].split("@");
                String[] textPositionHelperParts3 = textPositionHelperParts2[1].split(",");
                helper.add(TextPositionHelper.builder()
                        .text(textPositionHelperParts[0])
                        .prediction(textPositionHelperParts2[0])
                        .start(Integer.parseInt(textPositionHelperParts3[0]))
                        .end(Integer.parseInt(textPositionHelperParts3[1]))
                        .build());
            }
        }

        return PuzzlePiece.builder()
                .hash(hash)
                .x(x)
                .y(y)
                .width(width)
                .height(height)
                .helper(helper)
                .build();
    }

    @Override
    public String toString() {
        if (helper == null || helper.size() == 0) {
            return "%s#%d#%d#%s#%s".formatted(hash, x, y, formatDouble(width), formatDouble(height));
        } else {
            StringBuilder content = new StringBuilder("%s#%d#%d#%s#%s#".formatted(hash, x, y, formatDouble(width), formatDouble(height)));
            for (TextPositionHelper textPositionHelper : helper) {
                content.append(textPositionHelper.toString()).append("$");
            }
            return content.append("#").toString();
        }
    }
}
