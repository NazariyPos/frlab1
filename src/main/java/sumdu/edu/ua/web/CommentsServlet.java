package sumdu.edu.ua.web;

import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sumdu.edu.ua.db.CommentDao;

import java.io.IOException;

@MultipartConfig
public class CommentsServlet extends jakarta.servlet.http.HttpServlet {
    private final CommentDao dao = new CommentDao();
    private final com.fasterxml.jackson.databind.ObjectMapper om =
            new com.fasterxml.jackson.databind.ObjectMapper();
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CommentsServlet.class);

    /**
     * GET /comments
     * Очікувано: 200 OK + JSON масив коментарів.
     * Помилка БД: 500 + "DB error".
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json; charset=UTF-8");

        try {
            var comments = dao.list();
            om.writeValue(resp.getWriter(), comments);
        } catch (Exception e) {
            log.error("Database error on GET /comments", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "DB error");
        }
    }

    /**
     * POST /comments
     * Очікувано: 204 No Content при успіху.
     * Валідація: 400 (порожні/надто довгі поля або некоректний todoId).
     * Помилка БД: 500 + "DB error".
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");

        String author = req.getParameter("author");
        String text = req.getParameter("text");

        if (author == null || author.trim().isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Author is required");
            return;
        }
        if (author.length() > 64) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Author must be ≤ 64 characters");
            return;
        }
        if (text == null || text.trim().isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Text is required");
            return;
        }
        if (text.length() > 1000) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Text must be ≤ 1000 characters");
            return;
        }

        try {
            dao.add(author.trim(), text.trim());
            log.info("New comment added: author='{}', text length={}", author.trim(), text.trim().length());
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (Exception e) {
            log.error("Database error on POST /comments", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "DB error");
        }
    }
}