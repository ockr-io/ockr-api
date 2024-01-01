package io.ockr.ecosystem.controller;

import io.ockr.ecosystem.entity.api.ImageReadVerifyRequestBody;
import io.ockr.ecosystem.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/image/")
public class ImageController {

    @Autowired
    private ImageService imageService;

    @PostMapping("/content/restore")
    public ResponseEntity<?> readAndVerifyContent(@RequestBody ImageReadVerifyRequestBody requestBody) {
        String base64Image;
        try {
            base64Image = imageService.imageToBase64(requestBody.getFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (base64Image == null) {
            return ResponseEntity.badRequest().body("Could not read image");
        }

        return ResponseEntity.ok(imageService.getVerifiedContent(base64Image, requestBody.getQrCodeContent()));
    }
}
