package com.picpay.desafio_picpay.transaction;

import com.picpay.desafio_picpay.authorization.AuthorizerService;
import com.picpay.desafio_picpay.exception.InvalidTransactionException;
import com.picpay.desafio_picpay.notification.NotificationService;
import com.picpay.desafio_picpay.wallet.Wallet;
import com.picpay.desafio_picpay.wallet.WalletRepository;
import com.picpay.desafio_picpay.wallet.WalletType;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {
    @Mock
    TransactionRepository transactionRepository;

    @Mock
    WalletRepository walletRepository;

    @Mock
    AuthorizerService authorizerService;

    @Mock
    NotificationService notificationService;

    @InjectMocks
    TransactionService transactionService;

    @Test
    void create_debitsAndCreditsWallets_andSavesTransaction() {
        var payer = new Wallet(
            1L,
            "Payer",
            "123",
            "payer@email.com",
            "pw",
            WalletType.COMUN.getValue(),
            new BigDecimal("100.00")
        );
        var payee = new Wallet(
            2L,
            "Payee",
            "456",
            "payee@email.com",
            "pw",
            WalletType.COMUN.getValue(),
            new BigDecimal("10.00")
        );
        var transaction = new Transaction(
            10L,
            payer.id(),
            payee.id(),
            new BigDecimal("20.00"),
            null
        );

        when(walletRepository.findById(payer.id())).thenReturn(Optional.of(payer));
        when(walletRepository.findById(payee.id())).thenReturn(Optional.of(payee));
        when(walletRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(transactionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(authorizerService).authorize(transaction);
        doNothing().when(notificationService).notify(transaction);

        var saved = transactionService.create(transaction);

        ArgumentCaptor<Wallet> walletCaptor = ArgumentCaptor.forClass(Wallet.class);
        verify(walletRepository, org.mockito.Mockito.times(2)).save(walletCaptor.capture());

        var firstSaved = walletCaptor.getAllValues().get(0);
        var secondSaved = walletCaptor.getAllValues().get(1);

        assertThat(firstSaved.id()).isEqualTo(payer.id());
        assertThat(firstSaved.balance()).isEqualByComparingTo("80.00");
        assertThat(secondSaved.id()).isEqualTo(payee.id());
        assertThat(secondSaved.balance()).isEqualByComparingTo("30.00");

        verify(authorizerService).authorize(transaction);
        verify(notificationService).notify(transaction);
        verify(transactionRepository).save(transaction);

        assertThat(saved).isEqualTo(transaction);
    }

    @Test
    void validarTransacao_failsWhenPayerIsLojista() {
        var payer = new Wallet(
            1L,
            "Lojista",
            "123",
            "lojista@email.com",
            "pw",
            WalletType.LOJISTA.getValue(),
            new BigDecimal("100.00")
        );
        var payee = new Wallet(
            2L,
            "Payee",
            "456",
            "payee@email.com",
            "pw",
            WalletType.COMUN.getValue(),
            new BigDecimal("10.00")
        );
        var transaction = new Transaction(
            10L,
            payer.id(),
            payee.id(),
            new BigDecimal("20.00"),
            null
        );

        when(walletRepository.findById(payer.id())).thenReturn(Optional.of(payer));
        when(walletRepository.findById(payee.id())).thenReturn(Optional.of(payee));

        assertThatThrownBy(() -> transactionService.validarTransacao(transaction))
            .isInstanceOf(InvalidTransactionException.class)
            .hasMessageContaining("LOJISTA");
    }

    @Test
    void validarTransacao_failsWhenInsufficientBalance() {
        var payer = new Wallet(
            1L,
            "Payer",
            "123",
            "payer@email.com",
            "pw",
            WalletType.COMUN.getValue(),
            new BigDecimal("5.00")
        );
        var payee = new Wallet(
            2L,
            "Payee",
            "456",
            "payee@email.com",
            "pw",
            WalletType.COMUN.getValue(),
            new BigDecimal("10.00")
        );
        var transaction = new Transaction(
            10L,
            payer.id(),
            payee.id(),
            new BigDecimal("20.00"),
            null
        );

        when(walletRepository.findById(payer.id())).thenReturn(Optional.of(payer));
        when(walletRepository.findById(payee.id())).thenReturn(Optional.of(payee));

        assertThatThrownBy(() -> transactionService.validarTransacao(transaction))
            .isInstanceOf(InvalidTransactionException.class)
            .hasMessageContaining("Saldo insuficiente");
    }
}
