package sumdu.edu.ua.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sumdu.edu.ua.core.domain.Book;
import sumdu.edu.ua.core.domain.Comment;
import sumdu.edu.ua.core.domain.PageRequest;
import sumdu.edu.ua.web.config.Beans;

import java.io.IOException;
import java.util.List;

public class CommentsServlet extends HttpServlet {

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

        try {
            Book book = Beans.getCatalogService().findById(bookId);
            if (book == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "book not found");
                return;
            }
            List<Comment> comments = Beans.getCommentService()
                    .list(bookId, null, null, new PageRequest(0, 20))
                    .getItems();

            req.setAttribute("book", book);
            req.setAttribute("comments", comments);
            req.getRequestDispatcher("/WEB-INF/views/book-comments.jsp").forward(req, resp);
        } catch (Exception e) {
            throw new ServletException("Cannot load book details", e);
        }
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

        // "хак" для DELETE через HTML-форму
        if ("delete".equalsIgnoreCase(req.getParameter("_method"))) {
            long commentId;
            try {
                commentId = Long.parseLong(req.getParameter("commentId"));
            } catch (NumberFormatException e) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "invalid commentId");
                return;
            }
            try {
                Beans.getCommentService().delete(bookId, commentId);
                resp.sendRedirect(req.getContextPath() + "/comments?bookId=" + bookId);
            } catch (IllegalStateException e) {
                // правило 24 годин
                resp.sendError(HttpServletResponse.SC_CONFLICT, e.getMessage());
            } catch (IllegalArgumentException e) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
            } catch (Exception e) {
                throw new ServletException("Cannot delete comment", e);
            }
            return;
        }

        String author = req.getParameter("author");
        String text   = req.getParameter("text");

        try {
            Beans.getCommentService().add(bookId, author, text);
            resp.sendRedirect(req.getContextPath() + "/comments?bookId=" + bookId);
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ServletException("Cannot save comment", e);
        }
    }
}