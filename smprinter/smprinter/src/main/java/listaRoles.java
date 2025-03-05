import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/listaRoles")
public class listaRoles extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
        	//Establecer conexión con la base de datos
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/imprentadb", "root", "root");
            
            //Recuperar nombre de los roles
            PreparedStatement ps = con.prepareStatement("SELECT rol_nombre FROM roles");
            ResultSet rs = ps.executeQuery();

            //Enviar nombre de roles
            List<String> roles = new ArrayList<>();
            while (rs.next()) {
                roles.add(rs.getString("rol_nombre"));
            }

            con.close();

            StringBuilder rolesJson = new StringBuilder("[");
            for (int i = 0; i < roles.size(); i++) {
                rolesJson.append("\"").append(roles.get(i)).append("\"");
                if (i < roles.size() - 1) {
                    rolesJson.append(",");
                }
            }
            rolesJson.append("]");

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(rolesJson.toString());
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error al obtener la lista de roles");
        }
    }
}
