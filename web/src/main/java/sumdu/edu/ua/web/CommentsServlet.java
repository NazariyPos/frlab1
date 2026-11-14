package sumdu.edu.ua.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import sumdu.edu.ua.core.domain.Book;
import sumdu.edu.ua.core.domain.Comment;
import sumdu.edu.ua.core.domain.PageRequest;
import sumdu.edu.ua.core.service.CatalogService;
import sumdu.edu.ua.core.service.CommentService;

import java.io.IOException;
import java.util.List;

@Component
public class CommentsServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(CommentsServlet.class);

    private final CatalogService catalogService;
    private final CommentService commentService;

    public CommentsServlet(CatalogService catalogService, CommentService commentService) {
        this.catalogService = catalogService;
        this.commentService = commentService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String bookIdStr = req.getParameter("bookId");
        if (bookIdStr == null) {
            resp.sendRedirect(req.getContextPath() + "/books");
            return;
        }

        long bookId;
        try {
            bookId = Long.parseLong(bookIdStr);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "invalid bookId");
            return;
        }

        Book book = catalogService.findById(bookId);
        if (book == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "book not found");
            return;
        }

        List<Comment> comments = commentService
                .list(bookId, null, null, new PageRequest(0, 20))
                .getItems();

        req.setAttribute("book", book);
        req.setAttribute("comments", comments);
        req.getRequestDispatcher("/WEB-INF/views/book-comments.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        req.setCharacterEncoding("UTF-8");

        long bookId;
        try {
            bookId = Long.parseLong(req.getParameter("bookId"));
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "invalid bookId");
            return;
        }

        if ("delete".equalsIgnoreCase(req.getParameter("_method"))) {
            long commentId;
            try {
                commentId = Long.parseLong(req.getParameter("commentId"));
            } catch (NumberFormatException e) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "invalid commentId");
                return;
            }
            try {
                commentService.delete(bookId, commentId);
                resp.sendRedirect(req.getContextPath() + "/comments?bookId=" + bookId);
            } catch (IllegalStateException e) {
                log.warn("24h rule violation: {}", e.getMessage());
                resp.sendError(HttpServletResponse.SC_CONFLICT, e.getMessage());
            } catch (IllegalArgumentException e) {
                log.warn("Comment not found: {}", e.getMessage());
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
            }
            return;
        }

        String author = req.getParameter("author");
        String text   = req.getParameter("text");

        try {
            commentService.add(bookId, author, text);
            resp.sendRedirect(req.getContextPath() + "/comments?bookId=" + bookId);
        } catch (IllegalArgumentException e) {
            log.warn("Validation failed: {}", e.getMessage());
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }
}