package group11.medScriptAPI.util;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import group11.medScriptAPI.entity.Physician;
import group11.medScriptAPI.entity.Prescription;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;

/**
 * Generates a PDF representation of a prescription.
 * This class uses the iText library to create a PDF document that contains
 * details of a prescription including patient information, medication details,
 * and physician information.
 */
@Component
public class PrescriptionPdfGenerator {


    /**
     * Creates a PDF file for the given prescription using the specified template.
     *
     * @param prescription the prescription details to include in the PDF
     * @param templatePath the path to the PDF template
     * @param outputPath   the path where the generated PDF will be saved
     * @param physician    the physician associated with the prescription
     * @throws IOException if there is an error reading the template or writing the output file
     */
    public static void createPdf(Prescription prescription, String templatePath, String outputPath, Physician physician) throws IOException {

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(templatePath), new PdfWriter(outputPath));
        Document document = new Document(pdfDoc);
       // File signatureFile = Paths.get("src/main/resources/physicianSignatures/" + prescription.getPhysician().getId() + ".png").toAbsolutePath().toFile();
        PdfFont font = PdfFontFactory.createFont();

        // Formatter for date
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");



        addText(document, font, prescription.getPatient().getFullName()  , 130, 1000, 16, TextAlignment.LEFT);
        addText(document, font, prescription.getPatient().getAddress(), 130, 980, 16, TextAlignment.LEFT);
        addText(document, font, sdf.format(prescription.getCreationDate()), 810, 650, 30, TextAlignment.LEFT);
        addText(document, font, prescription.getMedication(), 140, 545, 20, TextAlignment.LEFT);
        addText(document, font, prescription.getTotalGrammage(), 140, 510, 20, TextAlignment.LEFT);
        addText(document, font, prescription.getText(), 140, 475, 20, TextAlignment.LEFT);
        addText(document, font, prescription.getPhysician().getFullName(), 1360, 520, 24, TextAlignment.CENTER);
        //addText(document, font, prescription.getPhysician().getSecondName(), 1370, 520, 24, TextAlignment.LEFT);
        addText(document, font, sdf.format(prescription.getPatient().getBirthDate()), 830, 900, 30, TextAlignment.LEFT);
        addText(document, font, prescription.getPatient().getInsurance().getInsurance_company(), 180, 740, 30, TextAlignment.LEFT);
        addText(document, font, prescription.getPatient().getInsurance().getNumber(), 400, 740, 30, TextAlignment.LEFT);
        addText(document, font, prescription.getPatient().getInsurance().getInsurance_company(), 150, 1090, 30, TextAlignment.LEFT);
        if(physician.getSignatureFile() != null) {
            System.out.println(physician.getSignatureFile().getAbsolutePath());
            addImage(document, physician.getSignatureFile().getAbsolutePath(), 1500, 300, 200, 100);
        }

        document.close();
    }
    /**
     * Adds text to the PDF document at a specified position with given font properties.
     *
     * @param document      the document to which the text will be added
     * @param font         the font to be used for the text
     * @param text         the text to be added
     * @param x            the x coordinate for the text position
     * @param y            the y coordinate for the text position
     * @param fontSize     the size of the font
     * @param textAlignment the alignment of the text
     */
    private static void addText(Document document, PdfFont font, String text, float x, float y, float fontSize, TextAlignment textAlignment) {
        Paragraph paragraph = new Paragraph(text)
                .setFont(font)
                .setFontSize(fontSize)
                .setFontColor(ColorConstants.BLACK)
                .setFixedPosition(x, y, 300)
                .setTextAlignment(textAlignment);
        document.add(paragraph);

    }
    /**
     * Adds an image to the PDF document at a specified position with given dimensions.
     *
     * @param document the document to which the image will be added
     * @param imagePath the path to the image file
     * @param x        the x coordinate for the image position
     * @param y        the y coordinate for the image position
     * @param width    the width of the image
     * @param height   the height of the image
     */
    private static void addImage(Document document, String imagePath, float x, float y, float width, float height) {
        ImageData imageData = null;
        try {
            imageData = ImageDataFactory.create(imagePath);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        Image image = new Image(imageData)
                .setFixedPosition(x, y)
                .scaleAbsolute(width, height);
        document.add(image);
    }
}