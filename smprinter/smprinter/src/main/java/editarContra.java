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
import java.util.Base64;

@WebServlet("/editarContra")
public class editarContra extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String usuarioEditar = request.getParameter("usuarioEditar");
    	String password = request.getParameter("password");
    	String passwordConfirmar = request.getParameter("passwordConfirmar");

    	if(password == passwordConfirmar) {
    		try {
                // Obtener el salt y el hash de la base de datos
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/imprentadb", "root", "root");
                PreparedStatement ps = con.prepareStatement("SELECT salt FROM usuarios WHERE usuario=?");
                ps.setString(1, usuarioEditar);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    String saltBase64 = rs.getString("salt");
                    byte[] salt = Base64.getDecoder().decode(saltBase64);

                    // Calcular el nuevo hash de la contraseña
                    byte[] hash = hashPassword(password.toCharArray(), salt);
                    String nuevaPasswordHash = Base64.getEncoder().encodeToString(hash);

                    // Actualizar la contraseña y el rol en la base de datos
                    ps = con.prepareStatement("UPDATE usuarios SET password=?, rol=?, usuario=? WHERE usuario=?");
                    ps.setString(1, nuevaPasswordHash);
                    ps.setString(2, usuarioEditar);
                    ps.executeUpdate();
                }
                con.close();

                response.sendRedirect("miembros.html?correcto=trueContra");
            } catch (Exception e) {
                e.printStackTrace();
            }
    	}else {
    		response.sendRedirect("miembros.html?correcto=falseContra");
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
}