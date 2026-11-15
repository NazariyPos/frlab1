package sumdu.edu.ua.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sumdu.edu.ua.core.domain.Book;
import sumdu.edu.ua.core.domain.Comment;
import sumdu.edu.ua.core.domain.Page;
import sumdu.edu.ua.core.domain.PageRequest;
import sumdu.edu.ua.core.service.CatalogService;
import sumdu.edu.ua.core.service.CommentService;
import sumdu.edu.ua.web.controller.dto.BookRequest;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/books")
public class BookController {

    private final CatalogService catalogService;
    private final CommentService commentService;

    public BookController(CatalogService catalogService, CommentService commentService) {
        this.catalogService = catalogService;
        this.commentService = commentService;
    }

    // GET /books?q=...&page=...&size=...
    @GetMapping
    public Page<Book> getBooks(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return catalogService.search(q, new PageRequest(page, size));
    }

    // GET /books/{id}
    @GetMapping("/{id}")
    public Map<String, Object> getBookById(@PathVariable long id) {
        Book book = catalogService.findById(id);
        if (book == null) {
            throw new IllegalArgumentException("Book not found");
        }

        List<Comment> comments = commentService
                .list(id, null, null, new PageRequest(0, 20))
                .getItems();

        return Map.of(
                "book", book,
                "comments", comments
        );
    }

    // POST /books
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Book addBook(@RequestBody BookRequest request) {
        return catalogService.add(request.title(), request.author(), request.pubYear());
    }
}