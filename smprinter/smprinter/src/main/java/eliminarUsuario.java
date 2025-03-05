import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/eliminarUsuario")
public class eliminarUsuario extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String usuario = request.getParameter("usuario");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/imprentadb", "root", "root");

            PreparedStatement ps = con.prepareStatement("DELETE FROM usuarios WHERE usuario=?");
            ps.setString(1, usuario);

            ps.executeUpdate();
            con.close();

            response.sendRedirect("miembros.html?correcto=trueEliminado");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
