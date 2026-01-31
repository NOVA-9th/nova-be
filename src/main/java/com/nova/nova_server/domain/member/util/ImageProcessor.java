package com.nova.nova_server.domain.member.util;

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

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

        // 로그로 압축 결과 확인
        System.out.println("Original size: " + file.getSize() + " bytes");
        System.out.println("Compressed size: " + result.length + " bytes");

        return result;
    }
}
