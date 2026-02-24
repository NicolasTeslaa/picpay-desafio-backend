package com.picpay.desafio_picpay.controller;

import com.picpay.desafio_picpay.transaction.Transaction;
import com.picpay.desafio_picpay.transaction.TransactionService;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {
    @Mock
    TransactionService transactionService;

    @InjectMocks
    TransactionController transactionController;

    @Test
    void getTransaction_returnsServiceList() {
        var transaction = new Transaction(
            1L,
            10L,
            20L,
            new BigDecimal("15.00"),
            null
        );
        when(transactionService.getTransactions()).thenReturn(List.of(transaction));

        var result = transactionController.getTransaction();

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(transaction);
    }

    @Test
    void createTransaction_delegatesToService() {
        var transaction = new Transaction(
            1L,
            10L,
            20L,
            new BigDecimal("15.00"),
            null
        );
        when(transactionService.create(transaction)).thenReturn(transaction);

        var result = transactionController.createTransaction(transaction);

        assertThat(result).isEqualTo(transaction);
    }
}
