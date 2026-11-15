package sumdu.edu.ua.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import sumdu.edu.ua.persistence.jdbc.DbInit;

@SpringBootApplication(scanBasePackages = "sumdu.edu.ua")
public class AppInit {
    public static void main(String[] args) {
        DbInit.init();
        SpringApplication.run(AppInit.class, args);
        System.out.println("Started at http://localhost:8080/books");
    }
}