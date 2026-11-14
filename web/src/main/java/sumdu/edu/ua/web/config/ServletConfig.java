package sumdu.edu.ua.web.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sumdu.edu.ua.web.BooksApiServlet;
import sumdu.edu.ua.web.BooksServlet;
import sumdu.edu.ua.web.CommentsServlet;

import java.io.IOException;

@Configuration
public class ServletConfig {

    @Bean
    public AppGreeting appGreeting(
            @Value("${app.greeting:Hello from Spring Boot!}") String message) {
        System.out.println(">>> Custom Bean [appGreeting] initialized: " + message);
        return new AppGreeting(message);
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    @Bean
    public ServletRegistrationBean<BooksServlet> booksServletRegistration(BooksServlet servlet) {
        return new ServletRegistrationBean<>(servlet, "/books/*");
    }

    @Bean
    public ServletRegistrationBean<CommentsServlet> commentsServletRegistration(CommentsServlet servlet) {
        return new ServletRegistrationBean<>(servlet, "/comments/*");
    }

    @Bean
    public ServletRegistrationBean<BooksApiServlet> booksApiServletRegistration(BooksApiServlet servlet) {
        return new ServletRegistrationBean<>(servlet, "/api/books/*");
    }

    @Bean
    public ServletRegistrationBean<HttpServlet> faviconServlet() {
        return new ServletRegistrationBean<>(new HttpServlet() {
            @Override
            protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT); // 204
            }
        }, "/favicon.ico");
    }

    @Bean
    public ServletRegistrationBean<HttpServlet> errorServlet() {
        return new ServletRegistrationBean<>(new HttpServlet() {
            @Override
            protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.setContentType("application/json;charset=UTF-8");
                resp.getWriter().write("{\"error\":\"Internal Server Error\"}");
            }
        }, "/error");
    }
}