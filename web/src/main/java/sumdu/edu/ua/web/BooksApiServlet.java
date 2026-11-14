package sumdu.edu.ua.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import sumdu.edu.ua.core.domain.Book;
import sumdu.edu.ua.core.domain.PageRequest;
import sumdu.edu.ua.core.service.CatalogService;

import java.io.IOException;

@Component
public class BooksApiServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(BooksApiServlet.class);

    private final CatalogService catalogService;
    private final ObjectMapper objectMapper;

    public BooksApiServlet(CatalogService catalogService, ObjectMapper objectMapper) {
        this.catalogService = catalogService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        int page = parseInt(req.getParameter("page"), 0);
        int size = parseInt(req.getParameter("size"), 10);
        String q = req.getParameter("q");

        try {
            var result = catalogService.search(q, new PageRequest(page, size));
            objectMapper.writeValue(resp.getWriter(), result);
        } catch (Exception e) {
            log.error("GET /api/books failed", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "DB error");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        try {
            Book book = objectMapper.readValue(req.getInputStream(), Book.class);
            Book saved = catalogService.add(book.getTitle(), book.getAuthor(), book.getPubYear());
            resp.setStatus(HttpServletResponse.SC_CREATED);
            objectMapper.writeValue(resp.getWriter(), saved);
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            log.error("POST /api/books failed", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "DB error");
        }
    }

    private int parseInt(String s, int def) {
        try {
            return (s != null) ? Integer.parseInt(s) : def;
        } catch (NumberFormatException e) {
            return def;
        }
    }
}