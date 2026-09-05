package com.sistemadebiblioteca;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.SistemaDeBiblioteca", "com.sistemadebiblioteca"})
public class SistemaDeBibliotecaApplication {

	public static void main(String[] args) {
		SpringApplication.run(SistemaDeBibliotecaApplication.class, args);
	}

}
