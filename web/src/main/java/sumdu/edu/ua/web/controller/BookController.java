package sumdu.edu.ua.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import sumdu.edu.ua.core.domain.Book;
import sumdu.edu.ua.core.domain.PageRequest;
import sumdu.edu.ua.core.service.CatalogService;

@Controller
@RequestMapping("/books")
public class BookController {

    private final CatalogService catalogService;

    public BookController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping
    public String getAllBooks(Model model) {
        var books = catalogService.search(null, new PageRequest(0, 20)).getItems();
        model.addAttribute("books", books);
        return "books"; // назва шаблону templates/books.html
    }

    @GetMapping("/new")
    public String showAddForm(Model model) {
        model.addAttribute("bookForm", new Book());
        return "book-add";
    }

    @PostMapping
    public String addBook(@ModelAttribute("bookForm") Book book) {
        catalogService.add(book.getTitle(), book.getAuthor(), book.getPubYear());
        return "redirect:/books";
    }
}