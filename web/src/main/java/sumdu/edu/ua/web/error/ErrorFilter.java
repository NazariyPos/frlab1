package sumdu.edu.ua.web.error;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ErrorFilter implements Filter {
    private static final Logger log = LoggerFactory.getLogger(ErrorFilter.class);

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletResponse httpResp = (HttpServletResponse) res;
        try {
            chain.doFilter(req, res);
        } catch (Exception e) {
            log.error("Unhandled error", e);
            if (!httpResp.isCommitted()) {
                httpResp.setStatus(500);
                httpResp.setContentType("application/json;charset=UTF-8");
                String msg = e.getMessage() == null ? "internal" : e.getMessage();
                httpResp.getWriter().write("{\"error\":\"internal\",\"message\":\""
                        + msg.replace("\"", "'") + "\"}");
            }
            return;
        }
        int status = httpResp.getStatus();
        if (status >= 400 && status < 500) {
            log.warn("Client error {} {} -> {}",
                    ((HttpServletRequest) req).getMethod(),
                    ((HttpServletRequest) req).getRequestURI(),
                    status);
        }
    }
}