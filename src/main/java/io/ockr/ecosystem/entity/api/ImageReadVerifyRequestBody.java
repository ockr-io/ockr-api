package io.ockr.ecosystem.entity.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;


@Getter
@Setter
@AllArgsConstructor
public class ImageReadVerifyRequestBody {
    MultipartFile file;
    String qrCodeContent;
}
