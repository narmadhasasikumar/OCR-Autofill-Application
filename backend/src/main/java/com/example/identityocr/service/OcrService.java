package com.example.identityocr.service;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.leptonica.PIX;
import org.bytedeco.tesseract.TessBaseAPI;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;

import static org.bytedeco.leptonica.global.leptonica.pixDestroy;
import static org.bytedeco.leptonica.global.leptonica.pixRead;

@Service
public class OcrService {

    public String extractText(MultipartFile file) throws Exception {

        File originalFile = Files.createTempFile("identity-original-", ".png").toFile();
        File processedFile = Files.createTempFile("identity-processed-", ".png").toFile();

        TessBaseAPI api = null;
        PIX image = null;
        BytePointer text = null;

        try {
            file.transferTo(originalFile);

            BufferedImage inputImage = ImageIO.read(originalFile);
            if (inputImage == null) {
                throw new RuntimeException("Could not read uploaded image.");
            }

            // 1. Enlarge image (2x)
            int scale = 2;
            int newWidth = inputImage.getWidth() * scale;
            int newHeight = inputImage.getHeight() * scale;

            BufferedImage resizedImage = new BufferedImage(
                    newWidth,
                    newHeight,
                    BufferedImage.TYPE_BYTE_GRAY
            );

            Graphics2D graphics = resizedImage.createGraphics();
            graphics.setRenderingHint(
                    RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BICUBIC
            );
            graphics.setRenderingHint(
                    RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY
            );
            graphics.drawImage(inputImage, 0, 0, newWidth, newHeight, null);
            graphics.dispose();

            // 2. Grayscale + Contrast (2x)
            BufferedImage processedImage = new BufferedImage(
                    newWidth,
                    newHeight,
                    BufferedImage.TYPE_BYTE_GRAY
            );

            for (int y = 0; y < newHeight; y++) {
                for (int x = 0; x < newWidth; x++) {
                    int rgb = resizedImage.getRGB(x, y);
                    int r = (rgb >> 16) & 0xff;
                    int g = (rgb >> 8) & 0xff;
                    int b = rgb & 0xff;

                    int gray = (int) (0.299 * r + 0.587 * g + 0.114 * b);

                    gray = (gray - 128) * 2 + 128;
                    gray = Math.max(0, Math.min(255, gray));

                    int newRgb = (gray << 16) | (gray << 8) | gray;
                    processedImage.setRGB(x, y, newRgb);
                }
            }

            ImageIO.write(processedImage, "png", processedFile);

            // 3. Initialize Tesseract
            api = new TessBaseAPI();
            String tessDataPath = new File("tessdata").getAbsolutePath();

            if (api.Init(tessDataPath, "eng") != 0) {
                throw new RuntimeException("Could not initialize Tesseract");
            }

            image = pixRead(processedFile.getAbsolutePath());
            if (image == null) {
                throw new RuntimeException("Could not load processed image.");
            }

            api.SetImage(image);
            api.SetPageSegMode(6);

            text = api.GetUTF8Text();
            if (text == null) {
                throw new RuntimeException("Tesseract returned no text.");
            }

            return text.getString();

        } finally {
            if (text != null) {
                text.deallocate();
            }
            if (api != null) {
                api.End();
            }
            if (image != null) {
                pixDestroy(image);
            }
            if (originalFile.exists()) {
                originalFile.delete();
            }
            if (processedFile.exists()) {
                processedFile.delete();
            }
        }
    }
}