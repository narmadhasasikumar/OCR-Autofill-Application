package com.example.identityocr.controller;

import com.example.identityocr.model.IdentityData;
import com.example.identityocr.service.IdentityParser;
import com.example.identityocr.service.OcrService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/identity")
@CrossOrigin(origins = "http://localhost:5173")
public class IdentityController {

    private final OcrService ocrService;
    private final IdentityParser identityParser;

    public IdentityController(
            OcrService ocrService,
            IdentityParser identityParser) {

        this.ocrService = ocrService;
        this.identityParser = identityParser;
    }

    @PostMapping("/scan")
    public ResponseEntity<?> scanIdentity(
            @RequestParam("file") MultipartFile file) {

        try {

            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body("No image file was uploaded.");
            }

            System.out.println(
                    "Received file: " + file.getOriginalFilename()
            );

            System.out.println(
                    "Content type: " + file.getContentType()
            );

            String ocrText =
                    ocrService.extractText(file);

            System.out.println("========== OCR TEXT ==========");
            System.out.println(ocrText);
            System.out.println("==============================");

            IdentityData data =
                    identityParser.parse(ocrText);

            return ResponseEntity.ok(data);

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity.internalServerError()
                    .body(
                            "OCR ERROR: "
                            + e.getClass().getSimpleName()
                            + " - "
                            + e.getMessage()
                    );
        }
    }

    @PostMapping("/submit")
    public ResponseEntity<?> submitIdentity(@RequestBody IdentityData identityData) {
        try {
            System.out.println("========== SUBMITTED IDENTITY DATA ==========");
            System.out.println("Name: " + identityData.getName());
            System.out.println("Date of Birth: " + identityData.getDateOfBirth());
            System.out.println("Gender: " + identityData.getGender());
            System.out.println("Identity Number: " + identityData.getIdentityNumber());
            System.out.println("Address: " + identityData.getAddress());
            System.out.println("=============================================");

            // TODO: Save 'identityData' to your database here using a repository/service

            return ResponseEntity.ok("Identity data submitted successfully!");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("SUBMIT ERROR: " + e.getMessage());
        }
    }
}