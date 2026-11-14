package sumdu.edu.ua.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sumdu.edu.ua.core.domain.PageRequest;
import sumdu.edu.ua.core.service.CatalogService;
import sumdu.edu.ua.web.config.AppGreeting;

import java.io.IOException;

@Component
public class BooksServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(BooksServlet.class);

    private final CatalogService catalogService;

    @Autowired
    private AppGreeting appGreeting;

    public BooksServlet(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        log.info("Field injection demo: {}", appGreeting.getMessage());

        PageRequest pr = new PageRequest(0, 20);
        req.setAttribute("books", catalogService.search(null, pr).getItems());
        req.getRequestDispatcher("/WEB-INF/views/books.jsp").forward(req, resp);
    }
}