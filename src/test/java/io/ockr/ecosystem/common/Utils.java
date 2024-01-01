package io.ockr.ecosystem.common;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class Utils {

    public static String loadFileContent(String filePath) throws IOException {
        try (InputStream inputStream = Utils.class.getClassLoader().getResourceAsStream(filePath)) {
            if (inputStream == null) {
                throw new IOException("Resource not found: " + filePath);
            }
            byte[] fileBytes = inputStream.readAllBytes();
            return new String(fileBytes, StandardCharsets.UTF_8);
        }
    }
}
