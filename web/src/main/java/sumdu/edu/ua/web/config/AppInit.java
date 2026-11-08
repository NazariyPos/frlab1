package sumdu.edu.ua.web.config;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppInit implements ServletContextListener {
    private static final Logger log = LoggerFactory.getLogger(AppInit.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Beans.init();
        log.info("App started at: http://localhost:8080/books");
    }
}