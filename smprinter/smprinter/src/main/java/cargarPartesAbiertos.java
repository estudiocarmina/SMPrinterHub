import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.sql.*;
import java.util.Date;

@WebServlet("/cargarPartesAbiertos")
public class cargarPartesAbiertos extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        try {
            // Establecer conexión a la base de datos dentro de un try-with-resources
            Class.forName("com.mysql.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/imprentadb", "root", "root")) {
                // Consulta SQL para recuperar los partes abiertos y sus asociaciones
                String sql = "SELECT p.id, p.observaciones, p.cliente, p.tipoCliente, p.fechaEntrega, pa.agrupacion_id " +
                             "FROM partes p LEFT JOIN partes_agrupaciones pa ON p.id = pa.parte_id " +
                             "WHERE p.estado='ABIERTO'";
                try (PreparedStatement statement = conn.prepareStatement(sql);
                     ResultSet rs = statement.executeQuery()) {
                    // Crear una lista de partes
                    StringBuilder partesHTML = new StringBuilder();
                    while (rs.next()) {
                        int id = rs.getInt("id");
                        String observaciones = rs.getString("observaciones");
                        String cliente = rs.getString("cliente");
                        String tipoCliente = rs.getString("tipoCliente");
                        Date fechaEntrega = rs.getDate("fechaEntrega");
                        int agrupacionId = rs.getInt("agrupacion_id"); // Puede ser null
                        // Agregar el parte al HTML
                        partesHTML.append("<div class='card' data-id='").append(id).append("' ")
                                  .append(agrupacionId > 0 ? "data-agrupacion-id='" + agrupacionId + "' " : "")
                                  .append("><div class='content'>")
                                  .append("<div class='leading-content'><div class='title'>").append(id).append("</div></div>")
                                  .append("<div class='detail'>").append(safeString(observaciones)).append("</div></div>")
                                  .append("<div class='trailing-content'><div class='option-tag-list'>")
                                  .append(createTag(safeString(cliente)))
                                  .append(createTag(safeString(tipoCliente)))
                                  .append(createTag(fechaEntrega != null ? fechaEntrega.toString() : ""))
                                  .append("</div></div></div>");
                    }
                    // Enviar los partes como respuesta al cliente
                    out.println(partesHTML.toString());
                }
            }
        } catch (ClassNotFoundException ex) {
            out.println("Error: Driver JDBC no encontrado");
            ex.printStackTrace(out);
        } catch (SQLException ex) {
            out.println("Error de SQL: " + ex.getMessage());
            ex.printStackTrace(out);
        } catch (Exception ex) {
            out.println("Error general: " + ex.getMessage());
            ex.printStackTrace(out);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
    
    private String createTag(String content) {
        return "<div class='tag2'><div class='leading-icon'><div class='icons-user-multiple2'>" +
                "<img class='elements21' src='img/miembros.svg'/></div></div><div class='label5'>" +
                (content != null ? content : "") + "</div></div>";
    }

    private String safeString(String value) {
        return value != null ? value : "";
    }
}