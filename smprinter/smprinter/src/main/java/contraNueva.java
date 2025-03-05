import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Base64;

@WebServlet("/contraNueva")
public class contraNueva extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Obtener los parámetros del formulario
        String nuevaPassword = request.getParameter("nuevaPassword");
        String confirmarPassword = request.getParameter("confirmarPassword");
        String usuario = request.getParameter("usuario");

        // Verificar que las contraseñas coincidan
        if (nuevaPassword.equals(confirmarPassword)) {
            try {
                // Obtener el salt de la base de datos
                byte[] salt = obtenerSalt(usuario);

                // Calcular el hash de la nueva contraseña
                byte[] hash = hashPassword(nuevaPassword.toCharArray(), salt);
                String nuevaPasswordHash = Base64.getEncoder().encodeToString(hash);

                // Actualizar la contraseña en la base de datos
                actualizarContraseña(usuario, nuevaPasswordHash);
                
                // Redirigir al usuario a la página de inicio de sesión
                response.sendRedirect("login.html?correcto=cambiada");
            } catch (Exception e) {
                e.printStackTrace();
                response.sendRedirect("error.html");
            }
        } else {
            // Las contraseñas no coinciden, manejar el error
            response.sendRedirect("login.html?error=noCoinciden");
        }
    }

    // Método para obtener el salt de la base de datos
    private byte[] obtenerSalt(String usuario) throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/imprentadb", "root", "root")) {
            String query = "SELECT salt FROM usuarios WHERE usuario=?";
            try (PreparedStatement ps = con.prepareStatement(query)) {
                ps.setString(1, usuario);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return Base64.getDecoder().decode(rs.getString("salt"));
                    }
                }
            }
        }
        return null; // Si el usuario no existe en la base de datos
    }

    // Método para calcular el hash de la contraseña utilizando PBKDF2
    private byte[] hashPassword(char[] password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        int iterations = 10000;
        int keyLength = 512; // Longitud del hash en bits
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLength);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        return skf.generateSecret(spec).getEncoded();
    }

    // Método para actualizar la contraseña en la base de datos
    private void actualizarContraseña(String usuario, String nuevaPasswordHash) throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/imprentadb", "root", "root")) {
            String query = "UPDATE usuarios SET password=? WHERE usuario=?";
            try (PreparedStatement ps = con.prepareStatement(query)) {
                ps.setString(1, nuevaPasswordHash);
                ps.setString(2, usuario);
                ps.executeUpdate();
            }
        }
    }
}
