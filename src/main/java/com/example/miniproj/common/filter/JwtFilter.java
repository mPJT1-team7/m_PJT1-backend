package com.example.miniproj.common.filter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter implements Filter {

    @Value("${jwt.secret}")
    private String secret;
    private Key key;

    @PostConstruct
    private void init() {
        System.out.println(">>>> JwtFilter init jwt secret : " + secret);
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    private final AntPathMatcher matcher = new AntPathMatcher();
    private static final List<String> WHITE_LIST_PATH = List.of(
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/users/signUp",
            "/users/signIn");

    // 토큰없이 접근가능한 endpoint 인지 아닌지 판단?
    public boolean isPath(String path) {
        return WHITE_LIST_PATH.stream()
                .anyMatch(pattern -> matcher.match(pattern, path));
    }

    //////////////////////////////

    @Override
    public void doFilter(ServletRequest request,
            ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        System.out.println(">>>> JwtFilter doFilter ");
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String endPoint = req.getRequestURI();
        System.out.println(">>>> JwtFilter User EndPoint : " + endPoint);
        String method = req.getMethod();
        System.out.println(">>>> JwtFilter Request Method : " + method);

        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
            res.setStatus(HttpServletResponse.SC_OK);
            res.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
            res.setHeader("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
            res.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");
            res.setHeader("Access-Control-Allow-Credentials", "true");

            chain.doFilter(request, response);
            return;
        }

        if (isPath(endPoint)) {
            System.out.println(">>>> JwtFilter " + endPoint + " 는 토큰없이 필터 통과");
            chain.doFilter(request, response);
            return;
        }

        // header , token 검증을 해서 통과 또는 리젝
        String authHeader = req.getHeader("Authorization");
        System.out.println(">>>> JwtFilter Authorization : " + authHeader);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println(">>>> JwtFilter No Authorization Header - Proceeding without authentication");
            chain.doFilter(request, response);
            return;
        }
        String token = authHeader.substring(7);
        System.out.println(">>>> JwtFilter token : " + token);
        System.out.println(">>>> JwtFilter token validation check ");
        try {
            // Token Validation & Claims Extraction
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String email = claims.getSubject();
            System.out.println(">>>> JwtFilter token validation success. User: " + email);

            // Create Authentication Token (Roles can be extracted from claims if available)
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(email, null,
                    List.of(new SimpleGrantedAuthority("ROLE_USER")));

            // Set Security Context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            chain.doFilter(request, response);

        } catch (Exception e) {
            System.out.println(">>>> JwtFilter token validation fail");
            return;
        }
    }

}
