package sumdu.edu.ua.core.service;

import sumdu.edu.ua.core.domain.Comment;
import sumdu.edu.ua.core.domain.Page;
import sumdu.edu.ua.core.domain.PageRequest;
import sumdu.edu.ua.core.port.CommentRepositoryPort;

import java.time.Duration;
import java.time.Instant;

public class CommentService {
    private final CommentRepositoryPort repo;

    public CommentService(CommentRepositoryPort repo) {
        this.repo = repo;
    }

    public void add(long bookId, String author, String text) {
        if (author == null || author.isBlank()) throw new IllegalArgumentException("author required");
        if (text == null || text.isBlank())     throw new IllegalArgumentException("text required");
        repo.add(bookId, author.trim(), text.trim());
    }

    public Page<Comment> list(long bookId, String author, Instant since, PageRequest request) {
        return repo.list(bookId, author, since, request);
    }

    /** Бізнес-правило: коментар можна видалити лише протягом 24 годин. */
    public void delete(long bookId, long commentId) {
        Comment c = repo.findById(bookId, commentId);
        if (c == null) throw new IllegalArgumentException("comment not found");
        if (Duration.between(c.getCreatedAt(), Instant.now()).toHours() > 24) {
            throw new IllegalStateException("Comment too old to delete");
        }
        repo.delete(bookId, commentId);
    }
}

