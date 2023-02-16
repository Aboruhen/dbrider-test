package com.javaclub.dbrider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class DbriderApplication {

	public static void main(String[] args) {
		SpringApplication.run(DbriderApplication.class, args);
	}

}
