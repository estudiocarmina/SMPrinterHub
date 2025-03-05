import jakarta.servlet.*;
import java.io.*;
import java.sql.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet("/duplicarParte")
public class duplicarParte extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Obtener el ID del parte de la solicitud
        int parteId = Integer.parseInt(request.getParameter("id"));

        try {
            // Crear conexión a la base de datos
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/imprentadb", "root", "root");

            // Obtener detalles del parte a duplicar
            String sql = "SELECT * FROM partes WHERE id=?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setInt(1, parteId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                // Insertar una nueva fila con los mismos datos pero ID diferente
                sql = "INSERT INTO partes (cliente, tipoCliente, nombreComercial, fecha, fechaEntrega, ubicacion, trabajo, producto, impresion, tintas, colorTinta, tamanho, papel, colorPapel, americanoOpcion, cantidad, formatoImpresion, hojas, observaciones, total, estado) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                statement.setString(1, resultSet.getString("cliente"));
                statement.setString(2, resultSet.getString("tipoCliente"));
                statement.setString(3, resultSet.getString("nombreComercial"));
                statement.setDate(4, resultSet.getDate("fecha"));
                statement.setDate(5, resultSet.getDate("fechaEntrega"));
                statement.setString(6, resultSet.getString("ubicacion"));
                statement.setString(7, resultSet.getString("trabajo"));
                statement.setString(8, resultSet.getString("producto"));
                statement.setString(9, resultSet.getString("impresion"));
                statement.setString(10, resultSet.getString("tintas"));
                statement.setString(11, resultSet.getString("colorTinta"));
                statement.setString(12, resultSet.getString("tamanho"));
                statement.setString(13, resultSet.getString("papel"));
                statement.setString(14, resultSet.getString("colorPapel"));
                statement.setString(15, resultSet.getString("americanoOpcion"));
                statement.setString(16, resultSet.getString("cantidad"));
                statement.setString(17, resultSet.getString("formatoImpresion"));
                statement.setString(18, resultSet.getString("hojas"));
                statement.setString(19, resultSet.getString("observaciones"));
                statement.setDouble(20, resultSet.getDouble("total"));
                statement.setString(21, resultSet.getString("estado"));
                
                statement.executeUpdate();

                // Obtener el ID del nuevo parte generado
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int nuevoParteId = generatedKeys.getInt(1);
                    // Redireccionar a la página de visualización del nuevo parte
                    response.sendRedirect("partesDeTrabajo.html");
                }
            }

        } catch (SQLException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }
}
