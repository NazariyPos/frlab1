package sumdu.edu.ua.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sumdu.edu.ua.core.service.CommentService;
import sumdu.edu.ua.web.controller.dto.CommentRequest;

import java.util.Map;

@RestController
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    // POST /comments
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, String> addComment(@RequestBody CommentRequest request) {
        commentService.add(request.bookId(), request.author(), request.text());
        return Map.of("status", "created");
    }

    // DELETE /comments/{bookId}/{commentId}
    @DeleteMapping("/{bookId}/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable long bookId, @PathVariable long commentId) {
        commentService.delete(bookId, commentId);
    }
}