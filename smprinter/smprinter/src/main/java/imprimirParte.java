import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.*;

@WebServlet("/imprimirParte")
public class imprimirParte extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int parteId = Integer.parseInt(request.getParameter("id"));

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/imprentadb", "root", "root");
             PreparedStatement ps = con.prepareStatement("SELECT * FROM partes WHERE id = ?")) {
            ps.setInt(1, parteId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                PDDocument document = new PDDocument();
                PDPage page = new PDPage();
                document.addPage(page);
                
                try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                    contentStream.beginText();
                    contentStream.setFont(PDType0Font.load(document, document.getClass().getResourceAsStream("/org/apache/pdfbox/resources/ttf/LiberationSans-Regular.ttf")), 14);
                    contentStream.newLineAtOffset(25, 750);
                    contentStream.showText("Información del Parte de Trabajo");
                    contentStream.setFont(PDType0Font.load(document, document.getClass().getResourceAsStream("/org/apache/pdfbox/resources/ttf/LiberationSans-Regular.ttf")), 12);
                    contentStream.newLineAtOffset(0, -15);

                    contentStream.showText("ID: " + rs.getInt("id"));
                    contentStream.newLineAtOffset(0, -15);
                    contentStream.showText("Cliente: " + rs.getString("cliente"));
                    contentStream.newLineAtOffset(0, -15);
                    contentStream.showText("Tipo Cliente: " + rs.getString("tipoCliente"));
                    contentStream.newLineAtOffset(0, -15);
                    contentStream.showText("Observaciones: " + rs.getString("observaciones"));
                    contentStream.newLineAtOffset(0, -15);
                    contentStream.showText("Fecha Entrega: " + rs.getDate("fechaEntrega"));
                    contentStream.newLineAtOffset(0, -15);
                    contentStream.showText("Ubicación: " + rs.getString("ubicacion"));
                    contentStream.newLineAtOffset(0, -15);
                    contentStream.showText("Trabajo: " + rs.getString("trabajo"));
                    contentStream.newLineAtOffset(0, -15);
                    contentStream.showText("Impresión: " + rs.getString("impresion"));
                    contentStream.newLineAtOffset(0, -15);
                    contentStream.showText("Producto: " + rs.getString("producto"));
                    contentStream.newLineAtOffset(0, -15);
                    contentStream.showText("Tintas: " + rs.getString("tintas"));
                    contentStream.newLineAtOffset(0, -15);
                    contentStream.showText("Tamaño: " + rs.getString("tamanho"));
                    contentStream.newLineAtOffset(0, -15);
                    contentStream.showText("Papel: " + rs.getString("papel"));
                    contentStream.newLineAtOffset(0, -15);
                    contentStream.showText("Color Papel: " + rs.getString("colorPapel"));
                    contentStream.newLineAtOffset(0, -15);
                    contentStream.showText("Cantidad: " + rs.getString("cantidad"));
                    contentStream.newLineAtOffset(0, -15);
                    contentStream.showText("Formato Impresión: " + rs.getString("formatoImpresion"));
                    contentStream.newLineAtOffset(0, -15);
                    contentStream.showText("Hojas: " + rs.getString("hojas"));
                    contentStream.newLineAtOffset(0, -15);
                    contentStream.showText("Total: " + rs.getDouble("total"));
                    contentStream.newLineAtOffset(0, -15);
                    contentStream.showText("Estado: " + rs.getString("estado"));
                    contentStream.endText();
                }

                response.setContentType("application/pdf");
                document.save(response.getOutputStream());
                document.close();
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Parte no encontrado");
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
