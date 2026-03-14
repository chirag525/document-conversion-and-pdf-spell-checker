package com.pdf.word.controller;

import com.pdf.word.model.SpellCheckResponse;
import com.pdf.word.service.PdfTextExtractorService;
import com.pdf.word.service.SpellCheckService;
import com.pdf.word.util.FileValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/spell-check")
@RequiredArgsConstructor
@CrossOrigin("*")
public class SpellCheckController {

    private final PdfTextExtractorService extractorService;
    private final SpellCheckService spellCheckService;

    @PostMapping
    public SpellCheckResponse checkSpelling(@RequestParam("file") MultipartFile file) {

        FileValidationUtil.validatePdf(file);

        String text = extractorService.extractText(file);

        return spellCheckService.checkSpelling(text, file.getOriginalFilename());
    }
}
