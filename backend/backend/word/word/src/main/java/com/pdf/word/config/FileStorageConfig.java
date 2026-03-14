package com.pdf.word.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;
import java.io.File;

@Configuration
@SuppressWarnings("unused")

public class FileStorageConfig {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @PostConstruct
    public void createUploadDirectory() {
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    public String getUploadDir() {
        return uploadDir;
    }
}

