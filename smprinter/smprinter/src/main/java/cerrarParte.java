import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.sql.*;

@WebServlet("/cerrarParte")
public class cerrarParte extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        Connection conn = null;
        try {
            int id = Integer.parseInt(request.getParameter("id"));

            // Establecer conexión a la base de datos
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/imprentadb", "root", "root");

            // Consulta SQL para actualizar el estado del parte
            String sql = "UPDATE partes SET estado='CERRADO' WHERE id=?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setInt(1, id);
            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated > 0) {
                out.println("Parte cerrado con éxito.");
            } else {
                out.println("No se encontró el parte.");
            }
        } catch (Exception ex) {
            out.println("Error: " + ex.getMessage());
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                out.println("Error al cerrar la conexión: " + ex.getMessage());
            }
            out.close();
        }
    }
}
