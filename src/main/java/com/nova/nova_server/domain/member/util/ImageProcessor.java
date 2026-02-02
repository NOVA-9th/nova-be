package com.nova.nova_server.domain.member.util;

import lombok.extern.slf4j.Slf4j; // Lombok 로그 어노테이션 임포트
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Slf4j
@Component
public class ImageProcessor {
    private static final int MAX_WIDTH = 500;
    private static final int MAX_HEIGHT = 500;
    private static final float QUALITY = 0.5f;

    public byte[] compressImage(MultipartFile file) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Thumbnails.of(file.getInputStream())
                .size(MAX_WIDTH, MAX_HEIGHT)
                .outputQuality(QUALITY)
                .outputFormat("jpg")
                .toOutputStream(outputStream);

        byte[] result = outputStream.toByteArray();

        log.info("Original size: {} bytes", file.getSize());
        log.info("Compressed size: {} bytes", result.length);

        return result;
    }
}