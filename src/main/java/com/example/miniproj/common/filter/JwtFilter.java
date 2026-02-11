package com.example.miniproj.common.filter;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import com.example.miniproj.common.exception.ErrorCode;
import com.example.miniproj.common.util.JwtProvider;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter implements Filter {

    private final JwtProvider jwtProvider;

    private final AntPathMatcher matcher = new AntPathMatcher();
    private static final List<String> WHITE_LIST_PATH = List.of(
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/users/signUp",
            "/users/signIn",
            "/v1/health/**",
            "/error");

    public boolean isPath(String path) {
        return WHITE_LIST_PATH.stream()
                .anyMatch(pattern -> matcher.match(pattern, path));
    }

    @Override
    public void doFilter(ServletRequest request,
            ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String endPoint = req.getRequestURI();
        
        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
            res.setStatus(HttpServletResponse.SC_OK);
            String origin = req.getHeader("Origin");
            if ("http://localhost:3000".equals(origin) || "http://localhost:5173".equals(origin)) {
                res.setHeader("Access-Control-Allow-Origin", origin);
            }
            res.setHeader("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
            res.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");
            res.setHeader("Access-Control-Allow-Credentials", "true");

            chain.doFilter(request, response);
            return;
        }

        if (isPath(endPoint)) {
            chain.doFilter(request, response);
            return;
        }

        String authHeader = req.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }
        
        String token = authHeader.substring(7);

        try {
            String email = jwtProvider.getUserEmailFromToken(token);
            
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(email, null,
                    List.of(new SimpleGrantedAuthority("ROLE_USER")));

            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (ExpiredJwtException e) {
            log.warn("Expired JWT Token: {}", e.getMessage());
            req.setAttribute("exception", ErrorCode.EXPIRED_TOKEN);
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Invalid JWT Token: {}", e.getMessage());
            req.setAttribute("exception", ErrorCode.INVALID_TOKEN);
        } catch (Exception e) {
            log.error("JWT Filter Internal Error: {}", e.getMessage());
            req.setAttribute("exception", ErrorCode.INTERNAL_SERVER_ERROR);
        }

        chain.doFilter(request, response);
    }

}
