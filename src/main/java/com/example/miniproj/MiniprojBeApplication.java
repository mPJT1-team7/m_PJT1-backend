package com.example.miniproj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class MiniprojBeApplication {
	public static void main(String[] args) {
		Dotenv env = Dotenv.configure().ignoreIfMissing().load();
		env.entries().forEach(entry -> 
			System.setProperty(entry.getKey(), entry.getValue())
		);
		SpringApplication.run(MiniprojBeApplication.class, args);
	}

} // ffff