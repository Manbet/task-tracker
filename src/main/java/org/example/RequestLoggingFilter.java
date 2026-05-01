package org.example;

import jakarta.servlet.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
@Slf4j
public class RequestLoggingFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        String requestUUID = UUID.randomUUID().toString();
        MDC.put("uuid", requestUUID);

        try {
            chain.doFilter(request, response);
        } finally {
            MDC.remove("uuid");
        }
    }
}
