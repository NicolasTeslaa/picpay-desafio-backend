package com.picpay.desafio_picpay.notification;

import com.picpay.desafio_picpay.transaction.Transaction;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationProducer {
    private final KafkaTemplate<String, Transaction> _kafkaTemplate;

    public NotificationProducer(KafkaTemplate<String, Transaction> kafkaTemplate) {
        _kafkaTemplate = kafkaTemplate;
    }

    public void send(Transaction transaction) {
        _kafkaTemplate.send("transaction-notification", transaction);
    }
}
