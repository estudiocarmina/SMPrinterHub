import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

@WebServlet("/crearUsuario")
public class crearUsuario extends HttpServlet {
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    String usuario = request.getParameter("usuario");
	    String password = request.getParameter("password");
	    String confirmarPassword = request.getParameter("confirmarPassword");
	    String rol = request.getParameter("rol");
	    String preg1 = request.getParameter("preg1");
	    
	    if(password.equals(confirmarPassword)) {
	    	try {
		        // Verificar si el rol proporcionado por el usuario existe en la base de datos
		        if (!validarRol(rol)) {
		            response.sendRedirect("usuarios.html?error=rol");
		            return;
		        }

		        // Generar un salt aleatorio
		        SecureRandom random = new SecureRandom();
		        byte[] salt = new byte[16];
		        random.nextBytes(salt);

		        // Calcular el hash de la contraseña usando PBKDF2
		        byte[] hash = hashPassword(password.toCharArray(), salt);

		        // Convertir el hash y el salt a cadenas base64 para almacenarlos en la base de datos
		        String hashBase64 = Base64.getEncoder().encodeToString(hash);
		        String saltBase64 = Base64.getEncoder().encodeToString(salt);

		        Class.forName("com.mysql.cj.jdbc.Driver");
		        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/imprentadb", "root", "root");

		        PreparedStatement ps = con.prepareStatement("INSERT INTO usuarios (usuario, password, salt, rol, preg1) VALUES (?, ?, ?, ?, ?)");
		        ps.setString(1, usuario);
		        ps.setString(2, hashBase64);
		        ps.setString(3, saltBase64);
		        ps.setString(4, rol);
		        ps.setString(5, preg1);

		        ps.executeUpdate();
		        con.close();

		        response.sendRedirect("miembros.html?correcto=true");
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
	    } else {
            response.sendRedirect("miembros.html?error=noCoinciden");
	    }
	    
	}

    // Método para generar el hash de la contraseña utilizando PBKDF2
    private byte[] hashPassword(char[] password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        int iterations = 10000;
        int keyLength = 512; // Longitud del hash en bits
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLength);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        return skf.generateSecret(spec).getEncoded();
    }
    
    // Método para validar si un rol existe en la base de datos
    private boolean validarRol(String rol) {
        boolean rolValido = false;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/imprentadb", "root", "root");

            PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM roles WHERE rol_nombre = ?");
            ps.setString(1, rol);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int count = rs.getInt(1);
                rolValido = count > 0;
            }

            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rolValido;
    }
}