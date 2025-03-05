import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.sql.*;
import java.util.Date;

@WebServlet("/cargarAgrupacion")
public class cargarAgrupacion extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        try {
            // Establecer conexión a la base de datos dentro de un try-with-resources
            Class.forName("com.mysql.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/imprentadb", "root", "root")) {
                // Consulta SQL para recuperar los partes abiertos
                String sql = "SELECT * FROM agrupaciones";
                try (PreparedStatement statement = conn.prepareStatement(sql);
                     ResultSet rs = statement.executeQuery()) {
                    // Crear una lista de partes
                    StringBuilder partesHTML = new StringBuilder();
                    while (rs.next()) {
                        int id = rs.getInt("id");
                        String nombre = rs.getString("nombre");
                        // Agregar el parte al HTML
                        partesHTML.append("<div class='empty-state3' data-id='").append(id).append("'><div class='frame-12'><div class='text3'>").append(nombre).append("</div></div><div class='overflow-menu'><div class='icon-button'><div class='icons-more-vertical'><img class='elements8' src='img/menu.svg'/></div></div></div>"
                                + "<div class='containerOverflow'><div class='state-default-type-single-select' onclick='abrirDialogoRenombrar(this)'><div class='icons-home-01Overflow'><img class='elementsOverflow' src='img/home.svg'/></div><div class='textOverflow'>Renombrar</div></div><div class='state-default-type-single-selectOverflow2' onclick='eliminarAgrupacion(this)'><div class='icons-home-01Overflow2'><img class='elementsOverflow2' src='img/borrar.svg'/></div><div class='textOverflow2'>Eliminar</div></div></div>")
                                .append("<div class='icons-arrow-up-01-sharp'><img class='elements9' src='img/flechaArriba.svg'/></div>")
                                .append("<div class='icons-arrow-down-01-sharp'><img class='elements10' src='img/flechaAbajo.svg'/></div>")
                                .append("<img class='icons-textarea-resize' src='img/resize.svg'/></div>");
                    }
                    // Enviar los partes como respuesta al cliente
                    out.println(partesHTML.toString());
                }
            }
        } catch (Exception ex) {
            out.println("Error: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            out.close();
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