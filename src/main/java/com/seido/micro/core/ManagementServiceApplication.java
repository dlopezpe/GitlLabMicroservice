package com.seido.micro.core;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * Spring boot application
 */
@ComponentScan(basePackages = "com.seido.micro.core")
@SpringBootApplication
public class ManagementServiceApplication {
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    /**
     * Spring boot launch method
     * @param args String[]
     */
    public static void main(String[] args) {
        SpringApplication.run(ManagementServiceApplication.class, args);
    }
    // configuring default locale

}
