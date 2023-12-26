package io.ockr.ecosystem.service;

import io.ockr.ecosystem.entity.TextPosition;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@SpringBootTest
@ComponentScan
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PdfServiceTest {

    @Autowired
    private PdfService pdfService;

    @Test
    public void extractTextPositionsTest() throws IOException {
        File file = new File("src/test/resources/pdfs/trust-tree.pdf");
        List<TextPosition> textPositions = pdfService.extractTextPositions(file);
        Assertions.assertEquals(102, textPositions.size());
        Assertions.assertEquals("authority", textPositions.get(0).getText());
        Assertions.assertEquals("5", textPositions.get(textPositions.size() - 1).getText());
        Assertions.assertEquals(1, textPositions.get(0).getPage());
    }

    @Test
    public void pageToBase64ImageTest() throws IOException {
        File file = new File("src/test/resources/pdfs/trust-tree.pdf");
        InputStream inputStream = FileUtils.openInputStream(file);
        String base64Image = pdfService.pageToBase64Image(inputStream, 0);
        Assertions.assertNotNull(base64Image);
        Assertions.assertTrue(
                base64Image.startsWith(
                        "/9j/4AAQSkZJRgABAgAAAQABAAD/2wBDAAgGBgcGBQgHBwcJCQgKDBQNDAsLDBkSEw8UHRofHh0aHBwgJC4nICIsI" +
                                "xwcKDcpLDAxNDQ0Hyc5PTgyPC4zNDL/"));
    }
}
