import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import java.sql.*;
import java.util.Arrays;

@WebServlet("/crearAgrupacion")
public class crearAgrupacion extends HttpServlet {
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String nombreAgrupacion = request.getParameter("nombreAgrupacion");
        
        // Imprimir información sobre la solicitud recibida para crear una agrupación
        System.out.println("Solicitud recibida para crear agrupación");
        System.out.println("Nombre de la agrupación: " + nombreAgrupacion);
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try {
            // Establecer una conexión con la base de datos
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/imprentadb", "root", "root");

            PreparedStatement stmt = conn.prepareStatement("INSERT INTO agrupaciones (nombre) VALUES (?)", Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, nombreAgrupacion);
            stmt.executeUpdate();

            // Cerrar la conexión a la base de datos
            conn.close();

            // Redireccionar a la página de creación de agrupación exitosa
            response.sendRedirect("espaciosTrabajo.html");
        } catch (Exception e) {
        	//Manejo de errores
            e.printStackTrace();
            out.println("Error al crear la agrupación.");
        }
    }
}
