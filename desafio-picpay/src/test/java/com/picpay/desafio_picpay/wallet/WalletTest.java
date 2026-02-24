package com.picpay.desafio_picpay.wallet;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jdbc.test.autoconfigure.DataJdbcTest;
import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.assertThat;

@DataJdbcTest
public class WalletTest{
        @Autowired
        WalletRepository walletRepository;

        @Test
        void shouldSaveAndFindById() {
            Wallet wallet = new Wallet(
                1L,
                "Joao Carlos",
                "49183829931",
                "email@email.com",
                "senha",
                WalletType.COMUN.getValue(),
                BigDecimal.valueOf(300)
            );

            walletRepository.save(wallet);

            var loaded = walletRepository.findById(1L);

            assertThat(loaded).isPresent();
            assertThat(loaded.get().fullName()).isEqualTo("Joao Carlos");
            assertThat(loaded.get().balance()).isEqualByComparingTo("300");
            assertThat(loaded.get().type()).isEqualTo(WalletType.COMUN.getValue());
        }
}
