package io.ockr.ecosystem.service;

import io.ockr.ecosystem.common.Utils;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

@DataJpaTest
@ComponentScan
public class ImageServiceTest {

    @Autowired
    ImageService imageService;

    @Test
    public void testImageToBase64() throws IOException {
        String base64Image = Utils.loadFileContent("text/example_1.txt");
        final MultipartFile multipartFile = new MockMultipartFile("example_1.png",
                "example_1.png", "image/png", FileUtils.openInputStream(
                Objects.requireNonNull(FileUtils.toFile(
                        this.getClass().getClassLoader()
                                .getResource("text/example_1.png")))));

        String base64Image2 = imageService.imageToBase64(multipartFile);
        Assertions.assertEquals(base64Image, base64Image2);
    }
}
