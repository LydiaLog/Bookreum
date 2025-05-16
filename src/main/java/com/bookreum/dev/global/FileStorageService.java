package com.bookreum.dev.global;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileStorageService {

    /** application.properties 에 정의된 upload.dir */
    @Value("${upload.dir}")
    private String uploadDir;

    /**
     * 1️⃣ MultipartFile 을 로컬에 저장하고,
     * 2️⃣ 저장된 파일의 접근 URL 경로를 리턴합니다.
     *
     * @param file 업로드 파일
     * @return /uploads/{uuid}.{ext} 형태의 URL 경로
     */
    public String storeFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        try {
            // 1) 업로드 디렉터리 생성
            Path dirPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(dirPath);

            // 2) 원본 확장자 추출
            String original = StringUtils.cleanPath(file.getOriginalFilename());
            String ext = "";
            int idx = original.lastIndexOf('.');
            if (idx > 0) {
                ext = original.substring(idx);
            }

            // 3) UUID 기반 고유 파일명
            String filename = UUID.randomUUID() + ext;
            Path target = dirPath.resolve(filename);

            // 4) 파일 저장
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            // 5) 브라우저가 접근할 수 있는 상대 URL 반환
            return "/uploads/" + filename;

        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패: " + e.getMessage(), e);
        }
    }
}
