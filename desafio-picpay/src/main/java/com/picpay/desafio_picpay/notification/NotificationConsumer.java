package com.picpay.desafio_picpay.notification;

import com.picpay.desafio_picpay.exception.NotificationException;
import com.picpay.desafio_picpay.transaction.Transaction;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class NotificationConsumer {
    private RestClient _restClient;

    public NotificationConsumer(RestClient.Builder builder) {
        _restClient = builder
            .baseUrl("https://util.devi.tools/api/v1/notify")
            .build();
    }

    @KafkaListener(topics = "transaction-notification", groupId = "picpay-desafio-backend")
    public void receiveNotification(Transaction transaction) {
        var response = _restClient.get()
            .retrieve()
            .toEntity(Notification.class);

        if(response.getStatusCode().isError()){
            throw new NotificationException("Error send notification");
        }
    }
}
