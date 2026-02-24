package com.picpay.desafio_picpay.transaction;

import com.picpay.desafio_picpay.authorization.AuthorizerService;
import com.picpay.desafio_picpay.exception.InvalidTransactionException;
import com.picpay.desafio_picpay.notification.NotificationService;
import com.picpay.desafio_picpay.wallet.WalletRepository;
import com.picpay.desafio_picpay.wallet.WalletType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TransactionService {
    private final TransactionRepository _transactionRepository;
    private final WalletRepository _walletRepository;
    private final AuthorizerService _authorizerService;
    private final NotificationService _notificationService;

    public TransactionService(
        TransactionRepository transactionRepository,
        WalletRepository walletRepository,
        AuthorizerService authorizerService,
        NotificationService notificationService
    ) {
        _transactionRepository = transactionRepository;
        _walletRepository = walletRepository;
        _authorizerService = authorizerService;
        _notificationService = notificationService;
    }

    public List<Transaction> getTransactions() {
        return _transactionRepository.findAll();
    }

    @Transactional
    public Transaction create(Transaction transaction) {
        // validate transaction
        validarTransacao(transaction);

        // debit payer wallet
        var walletPayer = _walletRepository.findById(transaction.payerId())
            .orElseThrow(() -> new InvalidTransactionException(
                "Não foi possível concluir a transação: a carteira do pagador não existe."
            ));
        _walletRepository.save(walletPayer.debit(transaction.value()));

        // credit payee wallet
        var walletPayee = _walletRepository.findById(transaction.payeeId())
            .orElseThrow(() -> new InvalidTransactionException(
                "Não foi possível concluir a transação: a carteira do recebedor não existe."
            ));
        _walletRepository.save(walletPayee.credit(transaction.value()));

        // external authorization
        _authorizerService.authorize(transaction);

        // notification
        _notificationService.notify(transaction);

        // return saved transaction (final state)
        return _transactionRepository.save(transaction);
    }

    public void validarTransacao(Transaction transaction) {
        if (transaction == null) {
            throw new InvalidTransactionException(
                "Não foi possível processar a transação: dados da transação não foram enviados."
            );
        }

        var payerId = transaction.payerId();
        var payeeId = transaction.payeeId();

        if (payerId == null || payeeId == null) {
            throw new InvalidTransactionException(
                "Não foi possível processar a transação: informe pagador e recebedor."
            );
        }

        if (payerId.equals(payeeId)) {
            throw new InvalidTransactionException(
                "Transação inválida: o pagador e o recebedor não podem ser a mesma pessoa."
            );
        }

        var payerWallet = _walletRepository.findById(payerId)
            .orElseThrow(() -> new InvalidTransactionException(
                "Não foi possível concluir a transação: pagador não encontrado."
            ));

        var payeeWallet = _walletRepository.findById(payeeId)
            .orElseThrow(() -> new InvalidTransactionException(
                "Não foi possível concluir a transação: recebedor não encontrado."
            ));

        // defensive check (should never happen due to orElseThrow, but keeps message clear)
        if (payerWallet == null || payeeWallet == null) {
            throw new InvalidTransactionException(
                "Não foi possível concluir a transação: não foi possível validar as carteiras."
            );
        }

        if (payerWallet.typeEnum() == WalletType.LOJISTA) {
            throw new InvalidTransactionException(
                "Transação não permitida: contas do tipo LOJISTA não podem enviar dinheiro."
            );
        }

        if (transaction.value() == null) {
            throw new InvalidTransactionException(
                "Não foi possível processar a transação: informe um valor para transferência."
            );
        }

        if (transaction.value().signum() <= 0) {
            throw new InvalidTransactionException(
                "Valor inválido: o valor da transferência precisa ser maior que zero."
            );
        }

        if (payerWallet.balance() == null) {
            throw new InvalidTransactionException(
                "Não foi possível concluir a transação: saldo do pagador indisponível."
            );
        }

        if (payerWallet.balance().compareTo(transaction.value()) < 0) {
            throw new InvalidTransactionException(
                "Saldo insuficiente: o pagador não possui saldo suficiente para concluir a transferência."
            );
        }
    }
}
