package sumdu.edu.ua.persistence.jdbc;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;

public final class DbInit {
    private DbInit() {}

    public static void init() {
        try (Connection c = Db.get();
             Statement st = c.createStatement()) {

            try (InputStream in = DbInit.class.getClassLoader()
                    .getResourceAsStream("schema.sql")) {
                if (in == null) {
                    throw new IllegalStateException("schema.sql not found in classpath");
                }
                String sql = new String(in.readAllBytes(), StandardCharsets.UTF_8);

                // Розбиваємо по ';', виконуємо кожен непорожній statement
                for (String cmd : sql.split(";")) {
                    if (!cmd.isBlank()) {
                        st.execute(cmd);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("DB schema init failed", e);
        }
    }
}