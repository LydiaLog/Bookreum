package com.bookreum.dev.global;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

import jakarta.servlet.MultipartConfigElement;

@Configuration
public class FileUploadConfig {

    /**
     * application.properties 에 정의된 upload.dir 값을 주입받습니다.
     * 예: upload.dir=C:/uploads
     */
    @Value("${upload.dir}")
    private String uploadDir;

    /**
     * Multipart 설정 빈 등록
     */
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        // 업로드 파일을 저장할 디렉토리
        factory.setLocation(uploadDir);
        // 최대 파일 크기
        factory.setMaxFileSize(DataSize.ofMegabytes(100));
        // 최대 요청 크기
        factory.setMaxRequestSize(DataSize.ofMegabytes(100));
        return factory.createMultipartConfig();
    }
}
