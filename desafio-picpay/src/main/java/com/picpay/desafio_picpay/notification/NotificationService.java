package com.picpay.desafio_picpay.notification;

import com.picpay.desafio_picpay.transaction.Transaction;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;


@Service
public class NotificationService {
    private NotificationProducer _notificationProducer;

    public NotificationService(NotificationProducer notificationProducer) {
        _notificationProducer = notificationProducer;
    }

    public void notify(Transaction transaction) {
        _notificationProducer.send(transaction);
    }
}
