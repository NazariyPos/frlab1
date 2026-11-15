package sumdu.edu.ua.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import sumdu.edu.ua.core.domain.PageRequest;
import sumdu.edu.ua.core.service.CatalogService;
import sumdu.edu.ua.core.service.CommentService;

@Controller
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;
    private final CatalogService catalogService;

    public CommentController(CommentService commentService, CatalogService catalogService) {
        this.commentService = commentService;
        this.catalogService = catalogService;
    }

    @GetMapping
    public String list(@RequestParam long bookId, Model model) {
        var book = catalogService.findById(bookId);
        if (book == null) return "redirect:/books";

        var comments = commentService
                .list(bookId, null, null, new PageRequest(0, 20))
                .getItems();

        model.addAttribute("book", book);
        model.addAttribute("comments", comments);
        return "book-comments";
    }

    @PostMapping
    public String add(@RequestParam long bookId,
                      @RequestParam String author,
                      @RequestParam String text) {
        commentService.add(bookId, author.trim(), text.trim());
        return "redirect:/comments?bookId=" + bookId;
    }

    @PostMapping("/delete")
    public String delete(@RequestParam long bookId,
                         @RequestParam long commentId) {
        commentService.delete(bookId, commentId);
        return "redirect:/comments?bookId=" + bookId;
    }
}