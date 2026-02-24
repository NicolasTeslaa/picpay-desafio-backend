package com.picpay.desafio_picpay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jdbc.repository.config.EnableJdbcAuditing;

// Ativa auditoria na camada de persistência, permitindo o uso de @CreatedDate e @LastModifiedDate
@EnableJdbcAuditing
@SpringBootApplication
public class DesafioPicpayApplication {

	public static void main(String[] args) {
		SpringApplication.run(DesafioPicpayApplication.class, args);
	}

}
