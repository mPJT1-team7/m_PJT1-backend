package com.example.miniproj.common.health;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/v1/health")
public class HealthController {

    @GetMapping("/check")
    public String check() {
        System.out.println("healthController : /check");
        return "test connection";
    }

}
