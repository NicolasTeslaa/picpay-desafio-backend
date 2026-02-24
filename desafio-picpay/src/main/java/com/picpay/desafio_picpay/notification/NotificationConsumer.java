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
        try {
            var response = _restClient.post()
                .body(transaction) // or a DTO with only what the API expects
                .retrieve()
                .toEntity(Notification.class);

            if (response.getStatusCode().isError()) {
                throw new NotificationException("Não foi possível enviar a notificação (HTTP " + response.getStatusCode() + ").");
            }

            System.out.println(response.getBody());
        } catch (org.springframework.web.client.HttpClientErrorException.NotFound ex) {
            // 404 means your configured route is wrong
            throw new NotificationException("Endpoint de notificação inválido (404). Verifique a URL/config do serviço.");
        } catch (org.springframework.web.client.RestClientResponseException ex) {
            // other 4xx/5xx
            throw new NotificationException("Falha ao enviar notificação (HTTP)");
        } catch (Exception ex) {
            throw new NotificationException("Falha inesperada ao enviar notificação.");
        }
    }
}
