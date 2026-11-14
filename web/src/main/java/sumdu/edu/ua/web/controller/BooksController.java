package sumdu.edu.ua.web.controller;

import io.javalin.Javalin;
import io.javalin.http.Context;
import sumdu.edu.ua.core.domain.Book;
import sumdu.edu.ua.core.domain.PageRequest;
import sumdu.edu.ua.core.service.CatalogService;
import sumdu.edu.ua.web.controller.dto.BookRequest;

import java.util.Map;

public class BooksController {

    private final CatalogService service;

    public BooksController(CatalogService service) {
        this.service = service;
    }

    public void registerRoutes(Javalin app) {
        app.get("/api/books", this::getBooks);
        app.get("/api/books/{id}", this::getBook);
        app.post("/api/books", this::addBook);
    }

    private void getBooks(Context ctx) {
        String q    = ctx.queryParam("q");
        int page    = ctx.queryParamAsClass("page", Integer.class).getOrDefault(0);
        int size    = ctx.queryParamAsClass("size", Integer.class).getOrDefault(10);

        var result = service.search(q, new PageRequest(page, size));
        ctx.json(result);
    }

    private void getBook(Context ctx) {
        long id;
        try {
            id = Long.parseLong(ctx.pathParam("id"));
        } catch (NumberFormatException e) {
            ctx.status(400).json(Map.of("error", "invalid id"));
            return;
        }
        Book book = service.findById(id);
        if (book == null) {
            ctx.status(404).json(Map.of("error", "book not found"));
            return;
        }
        ctx.json(book);
    }

    private void addBook(Context ctx) {
        BookRequest req = ctx.bodyAsClass(BookRequest.class);
        Book saved = service.add(req.title(), req.author(), req.pubYear());
        ctx.status(201).json(saved);
    }
}