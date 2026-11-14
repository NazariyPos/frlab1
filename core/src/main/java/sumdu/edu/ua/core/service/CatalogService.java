package sumdu.edu.ua.core.service;

import sumdu.edu.ua.core.domain.Book;
import sumdu.edu.ua.core.domain.Page;
import sumdu.edu.ua.core.domain.PageRequest;
import sumdu.edu.ua.core.port.CatalogRepositoryPort;
import org.springframework.stereotype.Service;

@Service
public class CatalogService {
    private final CatalogRepositoryPort repo;

    public CatalogService(CatalogRepositoryPort repo) {
        this.repo = repo;
    }

    public Page<Book> search(String query, PageRequest request) {
        return repo.search(query, request);
    }

    public Book findById(long id) {
        return repo.findById(id);
    }

    public Book add(String title, String author, int pubYear) {
        if (title == null || title.isBlank())   throw new IllegalArgumentException("title required");
        if (author == null || author.isBlank()) throw new IllegalArgumentException("author required");
        if (pubYear <= 0)                       throw new IllegalArgumentException("invalid pubYear");
        return repo.add(title.trim(), author.trim(), pubYear);
    }
}