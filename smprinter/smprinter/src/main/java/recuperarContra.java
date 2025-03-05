import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/recuperarContra")
public class recuperarContra extends HttpServlet {
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    String usuario = request.getParameter("usuario");
	    String respuesta1 = request.getParameter("respuesta1");
	    String respuesta2 = request.getParameter("respuesta2");
	    String respuesta3 = request.getParameter("respuesta3");

	    try {
	        verificarRespuestas(usuario, respuesta1, respuesta2, respuesta3, response);
	    } catch (Exception e) {
	        e.printStackTrace();
	        response.sendRedirect("login.html?error=database");
	    }
	}


	private void verificarRespuestas(String usuario, String respuesta1, String respuesta2, String respuesta3, HttpServletResponse response) throws SQLException, ClassNotFoundException, IOException {
	    Class.forName("com.mysql.cj.jdbc.Driver");
	    try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/imprentadb", "root", "root")) {
	        String query = "SELECT * FROM usuarios WHERE usuario=?";
	        try (PreparedStatement ps = con.prepareStatement(query)) {
	            ps.setString(1, usuario);
	            try (ResultSet rs = ps.executeQuery()) {
	                if (rs.next()) {
	                    // Verificar si las respuestas coinciden
	                    if (respuesta1.equals(rs.getString("preg1")) &&
	                        respuesta2.equals(rs.getString("preg2")) &&
	                        respuesta3.equals(rs.getString("preg3"))) {
	                        // Si las respuestas coinciden, redirigir al usuario al login
	                        response.sendRedirect("login.html?usuario=" + usuario + "&correcto=true");
	                    } else {
	                        // Si las respuestas no coinciden, redirigir a login.html con mensaje de error
	                        response.sendRedirect("login.html?error=respuestas");
	                    }
	                } else {
	                    // Si el usuario no existe, redirigir a login.html con mensaje de error
	                    response.sendRedirect("login.html?error=usuario_no_existe");
	                }
	            }
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	        response.sendRedirect("login.html?error=database");
	    }
	}

}
