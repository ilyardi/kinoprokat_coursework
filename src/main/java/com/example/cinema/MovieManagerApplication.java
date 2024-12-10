package com.example.cinema;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class MovieManagerApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(MovieManagerApplication.class, args);
    }

}
