import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.sql.*;

@WebServlet("/editarAgrupacion")
public class editarAgrupacion extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        Connection conn = null;
        try {
            // Establecer conexión a la base de datos
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/imprentadb", "root", "root");

            // Obtener los parámetros del formulario
            String nuevoNombre = request.getParameter("nuevoNombre");
            int agrupacionId = Integer.parseInt(request.getParameter("agrupacionId"));

            // Actualizar el nombre de la agrupación en la base de datos
            String sql = "UPDATE agrupaciones SET nombre = ? WHERE id = ?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, nuevoNombre);
            statement.setInt(2, agrupacionId);
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                out.println("Agrupación renombrada con éxito");
            } else {
                out.println("No se pudo renombrar la agrupación");
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
