import jakarta.servlet.*;
import java.io.*;
import java.sql.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet("/gestionPartes")

public class gestionPartes extends HttpServlet {
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String[] form = {"cliente", "tipoCliente", "nombreComercial", "fecha", "fechaEntrega", "ubicacion", "trabajo", "impresion", "producto", "tintas", "colorTinta", "tamanho", "papel", "colorPapel", "americanoOpcion", "cantidad", "formatoImpresion", "hojas", "observaciones", "total", "estado"};
		
		// Recuperar los parámetros del formulario
        for(int i = 0; i<form.length; i++) {
        	form[i] = request.getParameter(form[i]);
        }
        
        try {
            // Crear conexión a la base de datos
        	Class.forName("com.mysql.jdbc.Driver");
        	Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/imprentadb", "root", "root");
            // Verificar si la conexión se estableció correctamente
            System.out.println("Conexión establecida correctamente.");
            
            // Crear la consulta SQL para insertar los datos del formulario
            String sql = "INSERT INTO partes (cliente, tipoCliente, nombreComercial, fecha, fechaEntrega, ubicacion, trabajo, impresion, producto, tintas, colorTinta, tamanho, papel, colorPapel, americanoOpcion, cantidad, formatoImpresion, hojas, observaciones, total, estado) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = conn.prepareStatement(sql);
            
            for(int i=0; i<form.length; i++) {
            	if(form[i] != null && !form[i].isEmpty()) {
            		statement.setString(i+1, form[i]);
            	}else {
            		statement.setNull(i+1, Types.DATE);
            		statement.setNull(i+1, Types.NUMERIC);
            	}
            }

            // Ejecutar la inserción
            int filasInsertadas = statement.executeUpdate();
            if (filasInsertadas > 0) {
                // Los datos se han insertado correctamente
                response.sendRedirect("partesDeTrabajo.html");
            } else {
                // Ocurrió un error al insertar los datos 
                response.sendRedirect("error.html");
            }
        } catch (SQLException ex) {
            // Capturar la excepción SQL y mostrar el mensaje de error
            ex.printStackTrace();
        } catch (Exception e) {
            // Capturar otras excepciones y mostrar el mensaje de error
            e.printStackTrace();
        }
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}
}
