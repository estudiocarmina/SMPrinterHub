import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/imprimePartes")
public class imprimePartes extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        int page = Integer.parseInt(request.getParameter("page"));
        int itemsPerPage = Integer.parseInt(request.getParameter("itemsPerPage"));
        int offset = (page - 1) * itemsPerPage;
        String productoFiltrar = request.getParameter("productoFiltrar");

        try (PrintWriter out = response.getWriter()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");

                String query = "SELECT SQL_CALC_FOUND_ROWS * FROM partes";
                String countQuery = "SELECT FOUND_ROWS()";
                if (productoFiltrar != null && !productoFiltrar.isEmpty()) {
                    query += " WHERE producto = ?";
                }
                query += " LIMIT ? OFFSET ?";

                try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/imprentadb", "root", "root");
                     PreparedStatement ps = con.prepareStatement(query);
                     PreparedStatement countPs = con.prepareStatement(countQuery);
                ) {
                    int paramIndex = 1;
                    if (productoFiltrar != null && !productoFiltrar.isEmpty()) {
                        ps.setString(paramIndex++, productoFiltrar);
                    }
                    ps.setInt(paramIndex++, itemsPerPage);
                    ps.setInt(paramIndex, offset);

                    ResultSet rs = ps.executeQuery();
                    ResultSet countRs = countPs.executeQuery();

                    int totalItems = 0;
                    if (countRs.next()) {
                        totalItems = countRs.getInt(1);
                    }
                    int totalPages = (int) Math.ceil((double) totalItems / itemsPerPage);

                    StringBuilder userListHTML = new StringBuilder();
                    while (rs.next()) {
                        int id = rs.getInt("id");
                        String observaciones = rs.getString("observaciones");
                        String cliente = rs.getString("cliente");
                        String tipoCliente = rs.getString("tipoCliente");
                        Date fechaEntrega = rs.getDate("fechaEntrega");
                        String ubicacion = rs.getString("ubicacion");
                        String trabajo = rs.getString("trabajo");
                        String impresion = rs.getString("impresion");
                        String producto = rs.getString("producto");
                        String tintas = rs.getString("tintas");
                        String tamanho = rs.getString("tamanho");
                        String papel = rs.getString("papel");
                        String colorPapel = rs.getString("colorPapel");
                        String cantidad = rs.getString("cantidad");
                        String formatoImpresion = rs.getString("formatoImpresion");
                        String hojas = rs.getString("hojas");
                        double total = rs.getDouble("total");
                        String estado = rs.getString("estado");

                        userListHTML.append("<div class='card'><div class='content'>")
                                .append("<div class='leading-content'><div class='title'>").append(id).append("</div><div><div class='state-default-type-single-select'><div class='icons-home-01Overflow2'><img class='elementsOverflow2' src='img/editar.svg'/></div><div class='textOverflow2'>Editar</div></div><div class='state-default-type-single-select' onclick='imprimeParte(").append(id).append(")' data-id='").append(id).append("'><div class='icons-home-01Overflow2'><img class='elementsOverflow2' src='img/pdf.svg'/></div><div class='textOverflow2'>Imprimir</div></div><div class='state-default-type-single-select' onclick='duplicarParte(").append(id).append(")' data-id='").append(id).append("'><div class='icons-home-01Overflow2'><img class='elementsOverflow2' src='img/duplicar.svg'/></div><div class='textOverflow2'>Duplicar</div></div><div class='state-default-type-single-select' onclick='eliminarParte(").append(id).append(")' data-id='").append(id).append("'><div class='icons-home-01Overflow2'><img class='elementsOverflow3' src='img/borrar.svg' /></div><div class='textOverflow3'>Eliminar</div></div></div><div class='icon-button'><div class='icons-more-vertical'><img class='elementsOverflow' src='img/menu.svg'/></div></div></div>")
                                .append("<div class='detail'>").append(safeString(observaciones)).append("</div></div>")
                                .append("<div class='trailing-content'><div class='option-tag-list'>")
                                .append(createTag(safeString(cliente)))
                                .append(createTag(safeString(tipoCliente)))
                                .append(createTag(fechaEntrega != null ? fechaEntrega.toString() : ""))
                                .append("</div></div></div>");
                    }

                    out.println("<html><body>");
                    out.println(userListHTML.toString());
                    out.println("</body></html>");
                    out.println("<div id='pagination-info' data-total-pages='" + totalPages + "'></div>");
                }
            } catch (Exception e) {
                e.printStackTrace(out);
            }
        }
    }

    private String createTag(String content) {
        return "<div class='tag2'><div class='leading-icon'><div class='icons-user-multiple2'>" +
                "<img class='elements21' src='img/miembros.svg'/></div></div><div class='label5'>" +
                (content != null ? content : "") + "</div></div>";
    }

    private String safeString(String value) {
        return value != null ? value : "";
    }
}
