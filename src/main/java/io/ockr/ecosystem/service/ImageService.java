package io.ockr.ecosystem.service;

import io.ockr.ecosystem.algorithm.Parameter;
import io.ockr.ecosystem.algorithm.PuzzlePingPongAlgorithm;
import io.ockr.ecosystem.entity.HashResult;
import io.ockr.ecosystem.entity.TextPosition;
import io.ockr.ecosystem.enums.Algorithm;
import org.apache.pdfbox.rendering.ImageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;
import org.apache.http.entity.ContentType;
import java.util.List;

@Service
public class ImageService {

    @Autowired
    private ModelService modelService;

    public String imageToBase64(MultipartFile file) throws IOException {
        List<String> supportedTypes = List.of(
                ContentType.IMAGE_JPEG.getMimeType(), ContentType.IMAGE_PNG.getMimeType());

        if (!supportedTypes.contains(file.getContentType()) || file.getContentType() == null) {
            throw new RuntimeException("Unsupported file type");
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        BufferedImage image = ImageIO.read(file.getInputStream());
        ImageIO.write(image, file.getContentType().split("/")[1], byteArrayOutputStream);

        return Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
    }

    public List<TextPosition> getVerifiedContent(String base64Image, String qrCodeContent) {
        HashResult hashResult = HashResult.fromString(qrCodeContent);

        String algorithm = hashResult.getAlgorithm();
        List<TextPosition> textPositions;
        if (algorithm.equals(Algorithm.PUZZLE_PING_PONG.getName())) {
            List<Parameter> parameters = hashResult.getParameters();
            PuzzlePingPongAlgorithm puzzlePingPongAlgorithm = new PuzzlePingPongAlgorithm(modelService);
            for (Parameter parameter : parameters) {
                puzzlePingPongAlgorithm.setParameter(parameter.getName(), parameter.getValue());
            }
            textPositions = puzzlePingPongAlgorithm.extractTextPositions(base64Image);

            boolean isVerified = puzzlePingPongAlgorithm.verify(textPositions, hashResult);

            if (!isVerified) {
                throw new RuntimeException("Verification failed");
            }

            return textPositions;
        } else {
            throw new RuntimeException("Unsupported algorithm: " + algorithm);
        }
    }

}
