package com.example.adf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.AsyncConfigurer;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@SpringBootApplication
public class HackathonApplication extends SpringBootServletInitializer implements AsyncConfigurer{

	public static void main(String[] args) {
		SpringApplication.run(HackathonApplication.class, args);
	}
}
