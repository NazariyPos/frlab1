package sumdu.edu.ua.web;

import io.javalin.Javalin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sumdu.edu.ua.web.config.Beans;
import sumdu.edu.ua.web.controller.BooksController;
import sumdu.edu.ua.web.controller.CommentsController;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.javalin.json.JavalinJackson;

import java.util.Map;

public class JavalinBookApp {

    private static final Logger log = LoggerFactory.getLogger(JavalinBookApp.class);

    public static void main(String[] args) {
        Beans.init();

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        var app = Javalin.create(config -> {
            config.showJavalinBanner = false;
            config.jsonMapper(new JavalinJackson());
        });

        // === Middleware ===
        app.before(ctx -> log.info("→ {} {}", ctx.method(), ctx.path()));
        app.after (ctx -> log.info("← {} {} -> {}", ctx.method(), ctx.path(), ctx.statusCode()));

        // === Routes ===
        new BooksController(Beans.getCatalogService()).registerRoutes(app);
        new CommentsController(Beans.getCommentService()).registerRoutes(app);

        // === Centralized exception handling ===
        app.exception(IllegalArgumentException.class, (e, ctx) -> {
            log.warn("400 {} {}: {}", ctx.method(), ctx.path(), e.getMessage());
            ctx.status(400).json(Map.of("error", e.getMessage()));
        });
        app.exception(IllegalStateException.class, (e, ctx) -> {
            log.warn("409 {} {}: {}", ctx.method(), ctx.path(), e.getMessage());
            ctx.status(409).json(Map.of("error", e.getMessage()));
        });
        app.exception(Exception.class, (e, ctx) -> {
            log.error("500 {} {}", ctx.method(), ctx.path(), e);
            ctx.status(500).json(Map.of("error", "Internal server error"));
        });

        // 404 (не знайдено маршрут)
        app.error(404, ctx -> ctx.json(Map.of("error", "not found")));

        app.start(8080);
        log.info("Javalin app started: http://localhost:8080/api/books");
    }
}