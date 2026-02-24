package com.picpay.desafio_picpay.notification;

import com.picpay.desafio_picpay.exception.NotificationException;
import com.picpay.desafio_picpay.transaction.Transaction;
import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationConsumerTest {

    @Mock RestClient.Builder builder;

    @Test
    void receiveNotification_shouldThrowNotificationException_onUnexpectedError() {
        doReturn(builder).when(builder).baseUrl(anyString());
        var restClient = mock(RestClient.class);
        doThrow(new RuntimeException("boom")).when(restClient).post();

        var consumer = new NotificationConsumer(builder);
        ReflectionTestUtils.setField(consumer, "_restClient", restClient);

        var tx = new Transaction(1L, 10L, 20L, new BigDecimal("10.00"), null);

        assertThatThrownBy(() -> consumer.receiveNotification(tx))
            .isInstanceOf(NotificationException.class)
            .hasMessageContaining("Falha inesperada");
    }

    @Test
    void receiveNotification_shouldThrowNotificationException_on404() {
        doReturn(builder).when(builder).baseUrl(anyString());
        var restClient = mock(RestClient.class);
        doThrow(org.springframework.web.client.HttpClientErrorException.NotFound.create(
            HttpStatus.NOT_FOUND, "Not Found", null, null, null
        )).when(restClient).post();

        var consumer = new NotificationConsumer(builder);
        ReflectionTestUtils.setField(consumer, "_restClient", restClient);

        var tx = new Transaction(1L, 10L, 20L, new BigDecimal("10.00"), null);

        assertThatThrownBy(() -> consumer.receiveNotification(tx))
            .isInstanceOf(NotificationException.class)
            .hasMessageContaining("404");
    }
}
