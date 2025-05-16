package com.bookreum.dev.global;

import jakarta.servlet.MultipartConfigElement;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

/**
 * 파일 업로드 설정
 *  - application.properties 의 upload.dir 값을 읽어
 *    multipart.location 및 Tomcat basedir 에서 사용합니다.
 */
@Configuration
@RequiredArgsConstructor
public class FileUploadConfig {

    /**
     * application.properties 에 정의된 업로드 디렉터리
     * 예: C:/uploads
     */
    @Value("${upload.dir}")
    private final String uploadDir;

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();

        // 외부 절대경로를 그대로 사용
        factory.setLocation(uploadDir);

        // 파일 최대 크기 100MB
        factory.setMaxFileSize(DataSize.ofMegabytes(100));
        factory.setMaxRequestSize(DataSize.ofMegabytes(100));

        return factory.createMultipartConfig();
    }
}
