package com.picpay.desafio_picpay;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jdbc.repository.config.EnableJdbcAuditing;
import org.springframework.kafka.config.TopicBuilder;

// Ativa auditoria na camada de persistência, permitindo o uso de @CreatedDate e @LastModifiedDate
@EnableJdbcAuditing
@SpringBootApplication
public class DesafioPicpayApplication {

	public static void main(String[] args) {
		SpringApplication.run(DesafioPicpayApplication.class, args);
	}

    @Bean
    NewTopic notificationTopic() {
        return TopicBuilder.name("transaction-notification")
            .build();
    }
}
