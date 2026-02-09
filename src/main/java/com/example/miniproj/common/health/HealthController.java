package com.example.miniproj.common.health;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

import javax.sql.DataSource;
import java.sql.Connection;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/health")
@RequiredArgsConstructor
public class HealthController {

    private final DataSource dataSource;

    @GetMapping("/check")
    public String check() {
        System.out.println("healthController : /check");
        return "test connection";
    }

    @GetMapping("/db-check")
    public String dbCheck() {
        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(1)) {
                return "DB Connection OK";
            } else {
                return "DB Connection Failed: Invalid Connection";
            }
        } catch (Exception e) {
            return "DB Connection Failed: " + e.getMessage();
        }
    }

}
