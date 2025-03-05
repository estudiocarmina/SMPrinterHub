import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.util.Base64;

@WebServlet("/gestionLogin")
public class gestionLogin extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String usuario = request.getParameter("usuario");
        String password = request.getParameter("password");

        try {
            // Verificar las credenciales del usuario en la base de datos
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/imprentadb", "root", "root");
            PreparedStatement ps = con.prepareStatement("SELECT * FROM usuarios WHERE usuario=?");
            ps.setString(1, usuario);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // Si se encuentra el usuario en la base de datos, verificar la contraseña
                String storedHash = rs.getString("password");
                String saltBase64 = rs.getString("salt");
                byte[] salt = Base64.getDecoder().decode(saltBase64);

                // Calcular el hash de la contraseña ingresada por el usuario
                byte[] hash = hashPassword(password.toCharArray(), salt);
                String inputHash = Base64.getEncoder().encodeToString(hash);

                // Verificar si los hashes coinciden
                if (storedHash.equals(inputHash)) {
                    // Autenticación exitosa, crear sesión y redirigir al usuario
                    HttpSession session = request.getSession(true);
                    session.setAttribute("usuario", usuario);

                    // Establecer el tiempo de inactividad máximo de la sesión a 24 horas (1440 minutos)
                    session.setMaxInactiveInterval(1440 * 60); // en segundos

                    // Redirigir al usuario a la página principal
                    response.sendRedirect("partesDeTrabajo.html");
                } else {
                    // Contraseña incorrecta, redireccionar a la página de inicio de sesión con un mensaje de error
                    response.sendRedirect("login.html?error=invalid");
                }
            } else {
                // Usuario no encontrado, redireccionar a la página de inicio de sesión con un mensaje de error
                response.sendRedirect("login.html?error=notfound");
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
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
