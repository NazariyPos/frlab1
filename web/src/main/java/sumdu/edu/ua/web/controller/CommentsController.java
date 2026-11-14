package sumdu.edu.ua.web.controller;

import io.javalin.Javalin;
import io.javalin.http.Context;
import sumdu.edu.ua.core.domain.PageRequest;
import sumdu.edu.ua.core.service.CommentService;
import sumdu.edu.ua.web.controller.dto.CommentRequest;

import java.time.Instant;
import java.util.Map;

public class CommentsController {

    private final CommentService commentService;

    public CommentsController(CommentService commentService) {
        this.commentService = commentService;
    }

    public void registerRoutes(Javalin app) {
        app.get("/api/comments", this::listComments);
        app.post("/api/comments", this::addComment);
        app.delete("/api/books/{bookId}/comments/{commentId}", this::deleteComment);
    }

    private void listComments(Context ctx) {
        String bookIdStr = ctx.queryParam("bookId");
        if (bookIdStr == null) {
            ctx.status(400).json(Map.of("error", "bookId required"));
            return;
        }
        long bookId;
        try {
            bookId = Long.parseLong(bookIdStr);
        } catch (NumberFormatException e) {
            ctx.status(400).json(Map.of("error", "invalid bookId"));
            return;
        }

        String author   = ctx.queryParam("author");
        String sinceStr = ctx.queryParam("since");
        Instant since = null;
        if (sinceStr != null && !sinceStr.isBlank()) {
            try {
                since = Instant.parse(sinceStr);
            } catch (Exception e) {
                ctx.status(400).json(Map.of("error", "invalid since (ISO-8601 required)"));
                return;
            }
        }

        int page = ctx.queryParamAsClass("page", Integer.class).getOrDefault(0);
        int size = ctx.queryParamAsClass("size", Integer.class).getOrDefault(20);

        ctx.json(commentService.list(bookId, author, since, new PageRequest(page, size)));
    }

    private void addComment(Context ctx) {
        CommentRequest req = ctx.bodyAsClass(CommentRequest.class);
        commentService.add(req.bookId(), req.author(), req.text());
        ctx.status(201).json(Map.of("status", "created"));
    }

    private void deleteComment(Context ctx) {
        long bookId, commentId;
        try {
            bookId    = Long.parseLong(ctx.pathParam("bookId"));
            commentId = Long.parseLong(ctx.pathParam("commentId"));
        } catch (NumberFormatException e) {
            ctx.status(400).json(Map.of("error", "invalid id"));
            return;
        }
        commentService.delete(bookId, commentId);
        ctx.status(204);
    }
}