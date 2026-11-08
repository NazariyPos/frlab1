package sumdu.edu.ua.persistence.jdbc;

import sumdu.edu.ua.core.domain.Book;
import sumdu.edu.ua.core.domain.Page;
import sumdu.edu.ua.core.domain.PageRequest;
import sumdu.edu.ua.core.port.CatalogRepositoryPort;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class JdbcBookRepository implements CatalogRepositoryPort {

    @Override
    public Page<Book> search(String query, PageRequest request) {
        var items = new ArrayList<Book>();
        long total = 0;

        StringBuilder sql = new StringBuilder(
                "select id, title, author, pub_year from books where 1=1");
        if (query != null && !query.isBlank()) {
            sql.append(" and (lower(title) like ? or lower(author) like ?)");
        }
        sql.append(" order by id desc limit ? offset ?");

        try (Connection c = Db.get();
             PreparedStatement ps = c.prepareStatement(sql.toString())) {

            int i = 1;
            if (query != null && !query.isBlank()) {
                String pattern = "%" + query.toLowerCase() + "%";
                ps.setString(i++, pattern);
                ps.setString(i++, pattern);
            }
            ps.setInt(i++, request.getSize());
            ps.setInt(i,   request.getOffset());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(new Book(
                            rs.getLong("id"),
                            rs.getString("title"),
                            rs.getString("author"),
                            rs.getInt("pub_year")));
                }
            }

            try (PreparedStatement cnt = c.prepareStatement(
                    "select count(*) from books");
                 ResultSet rs = cnt.executeQuery()) {
                if (rs.next()) total = rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB query error (search books)", e);
        }

        return new Page<>(items, request, total);
    }

    @Override
    public Book findById(long id) {
        try (Connection c = Db.get();
             PreparedStatement ps = c.prepareStatement(
                     "select id, title, author, pub_year from books where id = ?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Book(
                            rs.getLong("id"),
                            rs.getString("title"),
                            rs.getString("author"),
                            rs.getInt("pub_year"));
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("DB query error (find book)", e);
        }
    }

    @Override
    public Book add(String title, String author, int pubYear) {
        try (Connection c = Db.get();
             PreparedStatement ps = c.prepareStatement(
                     "insert into books(title, author, pub_year) values (?,?,?)",
                     Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, title);
            ps.setString(2, author);
            ps.setInt(3, pubYear);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return new Book(keys.getLong(1), title, author, pubYear);
                }
            }
            throw new RuntimeException("Insert succeeded but no ID generated");
        } catch (SQLException e) {
            throw new RuntimeException("DB insert book failed", e);
        }
    }
}