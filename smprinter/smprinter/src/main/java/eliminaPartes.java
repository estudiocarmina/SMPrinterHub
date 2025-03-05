import jakarta.servlet.*;
import java.io.*;
import java.sql.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet("/eliminaPartes")
public class eliminaPartes extends HttpServlet {       
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Recuperar el parámetro del formulario
        int idParte = Integer.parseInt(request.getParameter("id"));
        
        try {
            // Crear conexión a la base de datos
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/imprentadb", "root", "root");
            
            // Crear la consulta SQL para eliminar el parte
            String sql = "DELETE FROM partes WHERE id = ?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setInt(1, idParte);
            
            // Ejecutar la eliminación
            int filasEliminadas = statement.executeUpdate();
            if (filasEliminadas > 0) {
                // El parte se ha eliminado correctamente
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                // No se encontró ningún parte con el ID especificado
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (SQLException ex) {
            // Capturar la excepción SQL y mostrar el mensaje de error
            ex.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            // Capturar otras excepciones y mostrar el mensaje de error
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
