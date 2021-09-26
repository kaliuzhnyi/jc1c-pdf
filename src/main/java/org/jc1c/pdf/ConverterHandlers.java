package org.jc1c.pdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.tools.PDFText2HTML;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.jc1c.annotations.JHandler;
import org.jc1c.annotations.JHandlerControllers;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@JHandlerControllers
public class ConverterHandlers {

    @JHandler(methodName = "convert_pdf_to_image")
    public static String[] handlerConvertPdfToImage(String data) throws IOException {
        return convertPdfToImage(data,
                ConverterSettings.OUTPUT_IMAGE_FORMAT,
                ConverterSettings.OUTPUT_IMAGE_DPI);
    }

    @JHandler(methodName = "convert_pdf_to_image")
    public static String[] handlerConvertPdfToImage(String data, String format, Long dpi) throws IOException {
        return convertPdfToImage(data,
                format.isEmpty() ? ConverterSettings.OUTPUT_IMAGE_FORMAT : format,
                dpi == 0 ? ConverterSettings.OUTPUT_IMAGE_DPI : dpi.intValue());
    }

    /**
     * @param data base64-string with binary data of pdf file
     * @param format the format of output images, valid values: jpeg, jpg, gif, tiff, png.
     * @return array base64-strings with binary data of image
     * @throws IOException
     */
    public static String[] convertPdfToImage(String data, String format, Integer dpi) throws IOException {

        List<String> result = new ArrayList<>();

        byte[] bytes = Base64.getDecoder().decode(data.getBytes());

        try(PDDocument pdfDocument = PDDocument.load(bytes)) {
            PDFRenderer pdfRenderer = new PDFRenderer(pdfDocument);
            for (int i = 0; i < pdfDocument.getNumberOfPages(); i++) {
                BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(i, dpi, ImageType.RGB);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                ImageIOUtil.writeImage(bufferedImage, format, stream, dpi);
                String base64Image = Base64.getEncoder().encodeToString(stream.toByteArray());
                result.add(base64Image);
            }
        }
        
        return result.toArray(new String[0]);
    }


    @JHandler(methodName = "convert_pdf_to_text")
    public static String handlerConvertPdfToText(String data) throws IOException {
        return convertPdfToText(data);
    }

    /**
     * @param data base64-string with binary data of pdf file
     * @return text extracted from PDF document
     * @throws IOException
     */
    public static String convertPdfToText(String data) throws IOException {

        String result = "";

        byte[] bytes = Base64.getDecoder().decode(data.getBytes());

        try(PDDocument pdfDocument = PDDocument.load(bytes)) {
            PDFTextStripper pdfTextStripper = new PDFTextStripper();
            result = pdfTextStripper.getText(pdfDocument);
        }

        return result;
    }


    @JHandler(methodName = "convert_pdf_to_html")
    public static String handlerConvertPdfToHtml(String data) throws IOException {
        return convertPdfToHtml(data);
    }

    /**
     * @param data base64-string with binary data of pdf file
     * @return html-text extracted from PDF document
     * @throws IOException
     */
    public static String convertPdfToHtml(String data) throws IOException {

        String result = "";

        byte[] bytes = Base64.getDecoder().decode(data.getBytes());

        try(PDDocument pdfDocument = PDDocument.load(bytes)) {
            PDFText2HTML pdfText2HTML = new PDFText2HTML();
            result = pdfText2HTML.getText(pdfDocument);
        }

        return result;
    }


}
