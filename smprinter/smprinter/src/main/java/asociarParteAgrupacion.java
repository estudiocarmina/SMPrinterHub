import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.sql.*;

@WebServlet("/asociarParteAgrupacion")
public class asociarParteAgrupacion extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String parteId = request.getParameter("parteId");
        String agrupacionId = request.getParameter("agrupacionId");
        
        System.out.println("Received request to associate Parte ID: " + parteId + " with Agrupación ID: " + agrupacionId);
        
        PrintWriter out = response.getWriter();
        response.setContentType("text/html");
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/imprentadb", "root", "root")) {
                // Verificar si el ID del parte ya está en la tabla partes_agrupaciones
                String selectSql = "SELECT * FROM partes_agrupaciones WHERE parte_id = ?";
                try (PreparedStatement selectStatement = conn.prepareStatement(selectSql)) {
                    selectStatement.setInt(1, Integer.parseInt(parteId));
                    try (ResultSet rs = selectStatement.executeQuery()) {
                        if (rs.next()) {
                            // Si el ID del parte ya existe en la tabla, actualizar la asociación existente
                            String updateSql = "UPDATE partes_agrupaciones SET agrupacion_id = ? WHERE parte_id = ?";
                            try (PreparedStatement updateStatement = conn.prepareStatement(updateSql)) {
                                updateStatement.setInt(1, Integer.parseInt(agrupacionId));
                                updateStatement.setInt(2, Integer.parseInt(parteId));
                                updateStatement.executeUpdate();
                                
                                System.out.println("Parte ID: " + parteId + " already associated with another Agrupación ID. Updated association.");
                                out.println("Parte ya asociado con éxito. Asociación actualizada.");
                            }
                        } else {
                            // Si el ID del parte no existe en la tabla, insertar una nueva asociación
                            String insertSql = "INSERT INTO partes_agrupaciones (parte_id, agrupacion_id) VALUES (?, ?)";
                            try (PreparedStatement insertStatement = conn.prepareStatement(insertSql)) {
                                insertStatement.setInt(1, Integer.parseInt(parteId));
                                insertStatement.setInt(2, Integer.parseInt(agrupacionId));
                                insertStatement.executeUpdate();
                                
                                System.out.println("Successfully associated Parte ID: " + parteId + " with Agrupación ID: " + agrupacionId);
                                out.println("Parte asociado con éxito");
                            }
                        }
                    }
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
}
