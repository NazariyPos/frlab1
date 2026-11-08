package sumdu.edu.ua.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sumdu.edu.ua.core.domain.PageRequest;
import sumdu.edu.ua.web.config.Beans;

import java.io.IOException;

public class BooksServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            PageRequest pr = new PageRequest(0, 20);
            req.setAttribute("books",
                    Beans.getCatalogService().search(null, pr).getItems());
            req.getRequestDispatcher("/WEB-INF/views/books.jsp").forward(req, resp);
        } catch (Exception e) {
            throw new ServletException("Cannot load books", e);
        }
    }
}