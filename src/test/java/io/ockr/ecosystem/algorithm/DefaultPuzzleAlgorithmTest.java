package io.ockr.ecosystem.algorithm;

import io.ockr.ecosystem.entity.HashResult;
import io.ockr.ecosystem.entity.TextPosition;
import io.ockr.ecosystem.service.PdfService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

import java.io.File;
import java.io.IOException;
import java.util.List;

@SpringBootTest
@ComponentScan
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DefaultPuzzleAlgorithmTest {

    @Autowired
    private PdfService pdfService;

    @Test
    public void extractTextPositionsTest() throws IOException {
        File file = new File("src/test/resources/pdfs/trust-tree.pdf");
        List<TextPosition> textPositions = pdfService.extractTextPositions(file);

        DefaultPuzzleAlgorithm defaultPuzzleAlgorithm = new DefaultPuzzleAlgorithm();
        HashResult hashResult = defaultPuzzleAlgorithm.compute(textPositions, null);

        Assertions.assertEquals(2, hashResult.getParameters().size());

        String qrCodeText = hashResult.toString();
        HashResult resconstruction = HashResult.fromString(qrCodeText);

        Assertions.assertTrue(qrCodeText.startsWith("9d483c52e62a39f129d2eb73537af488dcdac64e71c9f9a1ca6c93636b2b3b95"));
        Assertions.assertEquals(hashResult.getParameters().size(), resconstruction.getParameters().size());
        Assertions.assertEquals("xSlice", resconstruction.getParameters().get(0).getName());
        Assertions.assertEquals("3", resconstruction.getParameters().get(0).getValue());
        Assertions.assertEquals("ySlice", resconstruction.getParameters().get(1).getName());
        Assertions.assertEquals("3", resconstruction.getParameters().get(1).getValue());

    }
}
