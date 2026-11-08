package sumdu.edu.ua.web.config;

import sumdu.edu.ua.core.port.CatalogRepositoryPort;
import sumdu.edu.ua.core.port.CommentRepositoryPort;
import sumdu.edu.ua.core.service.CatalogService;
import sumdu.edu.ua.core.service.CommentService;
import sumdu.edu.ua.persistence.jdbc.DbInit;
import sumdu.edu.ua.persistence.jdbc.JdbcBookRepository;
import sumdu.edu.ua.persistence.jdbc.JdbcCommentRepository;

public final class Beans {
    private Beans() {}

    private static CatalogService catalogService;
    private static CommentService commentService;

    public static synchronized void init() {
        if (catalogService != null) return;

        DbInit.init();

        CatalogRepositoryPort bookRepo    = new JdbcBookRepository();
        CommentRepositoryPort commentRepo = new JdbcCommentRepository();

        catalogService = new CatalogService(bookRepo);
        commentService = new CommentService(commentRepo);
    }

    public static CatalogService getCatalogService() { return catalogService; }
    public static CommentService getCommentService() { return commentService; }
}