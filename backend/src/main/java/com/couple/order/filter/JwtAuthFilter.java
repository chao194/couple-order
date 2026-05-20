package com.couple.order.filter;

import com.couple.order.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Component
public class JwtAuthFilter implements Filter {

    @Autowired
    private JwtUtil jwtUtil;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final Set<String> PUBLIC_PATHS = Set.of(
            "/api/auth/login",
            "/api/auth/register",
            "/api/init/tables",
            "/api/init/seed"
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String path = httpRequest.getRequestURI();

        // Allow public paths
        if (PUBLIC_PATHS.contains(path) || path.startsWith("/api/menu") || path.startsWith("/api/categories") || path.startsWith("/api/files")) {
            chain.doFilter(request, response);
            return;
        }

        // Allow unauthenticated access to orders (GET/POST only)
        if (path.startsWith("/api/orders") && Set.of("GET", "POST").contains(httpRequest.getMethod())) {
            String authHeader = httpRequest.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                if (jwtUtil.validateToken(token)) {
                    httpRequest.setAttribute("userId", jwtUtil.getUserId(token));
                    httpRequest.setAttribute("username", jwtUtil.getUsername(token));
                    httpRequest.setAttribute("role", jwtUtil.getRole(token));
                }
            }
            chain.doFilter(request, response);
            return;
        }

        // Allow OPTIONS requests for CORS
        if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        String authHeader = httpRequest.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendError(httpResponse, 401, "未登录，请先登录");
            return;
        }

        String token = authHeader.substring(7);

        if (!jwtUtil.validateToken(token)) {
            sendError(httpResponse, 401, "登录已过期，请重新登录");
            return;
        }

        // Set user info as request attributes
        Long userId = jwtUtil.getUserId(token);
        String username = jwtUtil.getUsername(token);
        String role = jwtUtil.getRole(token);

        httpRequest.setAttribute("userId", userId);
        httpRequest.setAttribute("username", username);
        httpRequest.setAttribute("role", role);

        chain.doFilter(request, response);
    }

    private void sendError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("message", message);
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
