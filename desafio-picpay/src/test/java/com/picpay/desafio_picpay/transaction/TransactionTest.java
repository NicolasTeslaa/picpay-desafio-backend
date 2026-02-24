package com.picpay.desafio_picpay.transaction;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TransactionTest {
    @Test
    void constructor_setsScaleToTwoDecimals() {
        var transaction = new Transaction(
            1L,
            10L,
            20L,
            new BigDecimal("10"),
            null
        );

        assertThat(transaction.value().toPlainString()).isEqualTo("10.00");
    }
}
