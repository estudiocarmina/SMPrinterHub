

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet("/buscarMiembro")
public class buscarMiembro extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String searchTerm = request.getParameter("searchTerm");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/imprentadb", "root", "root");

            PreparedStatement ps = con.prepareStatement("SELECT * FROM usuarios WHERE usuario LIKE ?");
            ps.setString(1, "%" + searchTerm + "%");
            ResultSet rs = ps.executeQuery();

            // Generar el contenido de la lista de usuarios dentro de un div
            StringBuilder userListHTML = new StringBuilder();
            while (rs.next()) {
                String usuario = rs.getString("usuario");
                String rol = rs.getString("rol");

                // Botones de edición y eliminación con el nombre de usuario como parámetro
                userListHTML.append("<form class='formularioUser'><div class='particles-table-data-cel3Tabla'>").append("<div class='input-text2Tabla'>").append(usuario).append("</div></div>").append("<div class='particles-table-data-cel5Tabla'>").append("<div class='tagTabla'>").append("<div class='labelTabla'>").append(rol).append("</div></div></div>").append("<div class='particles-table-data-cel5Tabla'>").append("<div class='icon-buttonTabla' id='edicionUsuario'>").append("<div class='icons-editTabla'>").append("<img class='elements3Tabla' src='img/editar.svg' data-usuario='").append(usuario).append("'/>").append("</div></div>").append("<div class='icon-button2Tabla' id='eliminacionUsuario'>").append("<div class='icons-delete-02Tabla'>").append("<img class='elements4Tabla' src='img/borrar.svg' data-usuario='").append(usuario).append("'/>").append("</div></div></div></div></form>");
            }

            // Escribir el contenido generado dentro del div en la respuesta
            out.println("<html><body>");
            out.println(userListHTML.toString());
            out.println("</body></html>");
            
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
