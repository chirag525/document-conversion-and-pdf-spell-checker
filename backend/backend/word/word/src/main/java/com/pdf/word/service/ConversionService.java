package com.pdf.word.service;

import com.lowagie.text.Document;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfWriter;
import java.nio.file.Files;
import java.nio.file.Path;


import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripper;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.apache.poi.xslf.usermodel.*;


import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.Rectangle;
import java.io.*;

@Service
public class ConversionService {

  
    // WORD -> PDF 
  
    public byte[] wordToPdf(MultipartFile file) throws Exception {

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Uploaded file is empty.");
        }

        String originalName = file.getOriginalFilename();
        if (originalName == null ||
                !(originalName.endsWith(".doc") || originalName.endsWith(".docx"))) {
            throw new RuntimeException("Only .doc or .docx files are allowed.");
        }

        // Create temp directory safely
        File tempDir = Files.createTempDirectory("conversion_").toFile();

        // Create input file
        File inputFile = new File(tempDir, originalName);
        file.transferTo(inputFile);

        // LibreOffice path (CONFIRMED WORKING)
        String libreOfficePath =
                "C:\\Program Files\\LibreOffice\\program\\soffice.exe";

        ProcessBuilder builder = new ProcessBuilder(
                libreOfficePath,
                "--headless",
                "--convert-to", "pdf",
                "--outdir", tempDir.getAbsolutePath(),
                inputFile.getAbsolutePath()
        );

        builder.redirectErrorStream(true);

        Process process = builder.start();

        // Capture output (important for debugging)
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(process.getInputStream()));

        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }

        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new RuntimeException("LibreOffice conversion failed:\n" + output);
        }

        // Get generated PDF
        String pdfName = originalName.replaceAll("\\.docx?$", ".pdf");
        File pdfFile = new File(tempDir, pdfName);

        if (!pdfFile.exists()) {
            throw new RuntimeException("PDF file was not created.\nOutput:\n" + output);
        }

        byte[] pdfBytes = Files.readAllBytes(pdfFile.toPath());

        // Cleanup safely
        if (inputFile.exists() && !inputFile.delete()) {
            System.out.println("Warning: Could not delete input file");
        }

        if (pdfFile.exists() && !pdfFile.delete()) {
            System.out.println("Warning: Could not delete PDF file");
        }

        if (tempDir.exists() && !tempDir.delete()) {
            System.out.println("Warning: Could not delete temp directory");
        }


        return pdfBytes;
    }


   
    // PDF -> WORD

    public byte[] pdfToWord(MultipartFile file) throws Exception {

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Uploaded PDF is empty.");
        }

        String originalName = file.getOriginalFilename();
        if (originalName == null || !originalName.toLowerCase().endsWith(".pdf")) {
            throw new RuntimeException("Only .pdf files are allowed.");
        }

        Path tempDirPath = Files.createTempDirectory("pdf_to_word_");
        File tempDir = tempDirPath.toFile();

        String safeName = originalName.replaceAll("\\s+", "_");
        File inputFile = new File(tempDir, safeName);
        file.transferTo(inputFile);

        String libreOfficePath =
                "C:\\Program Files\\LibreOffice\\program\\soffice.exe";

        ProcessBuilder builder = new ProcessBuilder(
                libreOfficePath,
                "--headless",
                "--infilter=writer_pdf_import",
                "--convert-to", "docx:MS Word 2007 XML",
                "--outdir", tempDir.getAbsolutePath(),
                inputFile.getAbsolutePath()
        );

        builder.redirectErrorStream(true);

        Process process = builder.start();

        // IMPORTANT: wait properly
        boolean finished = process.waitFor(60, java.util.concurrent.TimeUnit.SECONDS);

        if (!finished) {
            process.destroyForcibly();
            throw new RuntimeException("LibreOffice conversion timed out.");
        }

        int exitCode = process.exitValue();

        if (exitCode != 0) {
            throw new RuntimeException("LibreOffice failed with exit code: " + exitCode);
        }

        // Wait a moment to ensure file is written
        Thread.sleep(1000);

        String docxName = safeName.replaceAll("\\.pdf$", ".docx");
        File docxFile = new File(tempDir, docxName);

        if (!docxFile.exists()) {
            throw new RuntimeException("DOCX file was not created.");
        }

        byte[] wordBytes = Files.readAllBytes(docxFile.toPath());

        // Cleanup
        try {
            Files.deleteIfExists(inputFile.toPath());
            Files.deleteIfExists(docxFile.toPath());
            Files.deleteIfExists(tempDir.toPath());
        } catch (Exception ignored) {}

        return wordBytes;
    }



    
    // PDF -> POWERPOINT
 
    public byte[] pdfToPowerPoint(MultipartFile file) throws Exception {

        try (PDDocument pdf = PDDocument.load(file.getInputStream());
             XMLSlideShow ppt = new XMLSlideShow();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            String text = new PDFTextStripper().getText(pdf);
            String[] lines = text.split("\n");

            int index = 0;

            while (index < lines.length) {

                XSLFSlide slide = ppt.createSlide();
                XSLFTextBox box = slide.createTextBox();
                box.setAnchor(new Rectangle(50, 50, 620, 400));

                XSLFTextParagraph para = box.addNewTextParagraph();
                int count = 0;

                while (count < 6 && index < lines.length) {
                    if (!lines[index].trim().isEmpty()) {
                        XSLFTextRun run = para.addNewTextRun();
                        run.setText(lines[index]);
                        run.setFontSize(18.0);
                        count++;
                    }
                    index++;
                }
            }

            ppt.write(out);
            return out.toByteArray();
        }
    }

   
    // IMAGE -> PDF

    public byte[] imageToPdf(MultipartFile file) throws Exception {

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Document pdf = new Document(PageSize.A4);
            PdfWriter.getInstance(pdf, out);
            pdf.open();

            Image img = Image.getInstance(file.getBytes());
            img.scaleToFit(PageSize.A4.getWidth() - 80, PageSize.A4.getHeight() - 80);
            img.setAlignment(Image.ALIGN_CENTER);

            pdf.add(img);
            pdf.close();

            return out.toByteArray();
        }
    }

 
    // PDF -> EXCEL
 
    public byte[] pdfToExcel(MultipartFile file) throws Exception {

        try (PDDocument pdf = PDDocument.load(file.getInputStream());
             Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            String text = new PDFTextStripper().getText(pdf);
            Sheet sheet = workbook.createSheet("Data");

            int rowNum = 0;
            for (String line : text.split("\n")) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(line);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

   
    // ROTATE PDF

    public byte[] rotatePdf(MultipartFile file, int angle) throws Exception {

        if (angle % 90 != 0) {
            throw new IllegalArgumentException("Angle must be multiple of 90");
        }

        try (PDDocument pdf = PDDocument.load(file.getInputStream());
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            for (PDPage page : pdf.getPages()) {
                page.setRotation((page.getRotation() + angle) % 360);
            }

            pdf.save(out);
            return out.toByteArray();
        }
    }

    
    // MERGE PDFs

    public byte[] mergePdfs(MultipartFile[] files) throws Exception {

        PDFMergerUtility merger = new PDFMergerUtility();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        for (MultipartFile f : files) {
            merger.addSource(f.getInputStream());
        }

        merger.setDestinationStream(out);
        merger.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());

        return out.toByteArray();
    }
}
