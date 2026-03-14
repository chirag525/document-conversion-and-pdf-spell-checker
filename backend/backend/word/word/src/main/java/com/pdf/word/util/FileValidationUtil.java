package com.pdf.word.util;

import com.pdf.word.exception.FileProcessingException;
import org.springframework.web.multipart.MultipartFile;

public class FileValidationUtil {

    public static void validatePdf(MultipartFile file) {

        if (file.isEmpty()) {
            throw new FileProcessingException("File is empty");
        }

        if (!file.getOriginalFilename().toLowerCase().endsWith(".pdf")) {
            throw new FileProcessingException("Only PDF files are allowed");
        }
    }
}
