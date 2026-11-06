package mx.fes.unam.zacatlan.service.venta;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import mx.fes.unam.zacatlan.model.entities.venta.VentaDTO;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class PdfVentaService {

    public byte[] generatePdf(List<VentaDTO> lista, String titulo) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 36, 36, 36, 36);
        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();

            Paragraph title = new Paragraph(titulo);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);

            // Encabezados
            String[] headers = {"Fecha", "Producto", "Cantidad", "Total", "Vendedor"};
            for (String h : headers) {
                PdfPCell celda = new PdfPCell(new Phrase(h));
                celda.setBackgroundColor(new Color(167, 218, 255));
                celda.setPadding(6f);
                table.addCell(celda);
            }

            // Filas
            for (VentaDTO v : lista) {
                table.addCell(v.getFecha() != null ? v.getFecha().toString() : "");
                table.addCell(v.getProducto() != null ? v.getProducto().getNombre() : "");
                table.addCell(String.valueOf(v.getCantidad()));
                table.addCell(String.valueOf(v.getTotal()));
                table.addCell(v.getVendedor() != null ? v.getVendedor().getNombre() : "");
            }

            document.add(table);
            document.close();
            return outputStream.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error al generar el PDF", e);
        }
    }
}