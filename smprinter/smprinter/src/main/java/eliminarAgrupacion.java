import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import java.sql.*;

@WebServlet("/eliminarAgrupacion")
public class eliminarAgrupacion extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int agrupacionId = Integer.parseInt(request.getParameter("id"));

        try {
            // Establecer conexión con la base de datos
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/imprentadb", "root", "root");

            // Eliminar agrupación
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM agrupaciones WHERE id = ?");
            stmt.setInt(1, agrupacionId);
            int rowsAffected = stmt.executeUpdate();

            // Enviar respuesta al cliente
            if (rowsAffected > 0) {
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
