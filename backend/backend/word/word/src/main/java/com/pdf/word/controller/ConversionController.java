package com.pdf.word.controller;

import com.pdf.word.service.ConversionService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/convert")
@CrossOrigin
public class ConversionController {

    private final ConversionService service;

    public ConversionController(ConversionService service) {
        this.service = service;
    }

    // ✅ FIXED HERE
    @PostMapping("/word-to-pdf")
    public ResponseEntity<byte[]> convert(
            @RequestParam("file") MultipartFile file) throws Exception {

        byte[] pdf = service.wordToPdf(file);  // ✅ Correct way

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=converted.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @PostMapping("/pdf-to-word")
    public ResponseEntity<byte[]> pdfToWord(
            @RequestParam("file") MultipartFile file) throws Exception {

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=output.docx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(service.pdfToWord(file));
    }

    @PostMapping("/pdf-to-ppt")
    public ResponseEntity<byte[]> pdfToPpt(
            @RequestParam("file") MultipartFile file) throws Exception {

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=output.pptx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(service.pdfToPowerPoint(file));
    }

    @PostMapping("/image-to-pdf")
    public ResponseEntity<byte[]> imageToPdf(
            @RequestParam("file") MultipartFile file) throws Exception {

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=image.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(service.imageToPdf(file));
    }

    @PostMapping("/pdf-to-excel")
    public ResponseEntity<byte[]> pdfToExcel(
            @RequestParam("file") MultipartFile file) throws Exception {

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=data.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(service.pdfToExcel(file));
    }

    @PostMapping("/rotate-pdf")
    public ResponseEntity<byte[]> rotatePdf(
            @RequestParam("file") MultipartFile file,
            @RequestParam("angle") int angle) throws Exception {

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=rotated.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(service.rotatePdf(file, angle));
    }

    @PostMapping("/merge-pdfs")
    public ResponseEntity<byte[]> mergePdf(
            @RequestParam("files") MultipartFile[] files) throws Exception {

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=merged.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(service.mergePdfs(files));
    }
}
