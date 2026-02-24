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

    public TransactionService(TransactionRepository transactionRepository, WalletRepository walletRepository, AuthorizerService authorizerService, NotificationService notificationService) {
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
        // valida transação
        validarTransacao(transaction);

        // cria transação
        _transactionRepository.save(transaction);

        // debitar carteira pagador
        var walletPayer = _walletRepository.findById(transaction.payerId()).get();
        _walletRepository.save(walletPayer.debit(transaction.value()));

        var walletPayee = _walletRepository.findById(transaction.payeeId()).get();
        _walletRepository.save(walletPayee.credit(transaction.value()));

        // chamar serviços externos
        _authorizerService.authorize(transaction);

        // notificação
        _notificationService.notify(transaction);

        return _transactionRepository.save(transaction);
    }

    public void validarTransacao(Transaction transaction) {
        if(transaction == null) throw new InvalidTransactionException("Transação vazia");

        var payerId = transaction.payerId();
        var payeeId = transaction.payeeId();

        if(payerId == null || payeeId == null)
            throw new InvalidTransactionException("Transação inválida");

        if(payerId.equals(payeeId))
            throw new InvalidTransactionException("Pagador e Recebedor igual");

        var payerWallet = _walletRepository.findById(payerId)
            .orElseThrow(() -> new InvalidTransactionException("Pagador não encontrado"));

        var payeeWallet = _walletRepository.findById(payeeId)
            .orElseThrow(() -> new InvalidTransactionException("Recebedor não encontrado"));

        if(payerWallet == null || payeeWallet == null)
            throw  new InvalidTransactionException("Transação vazia");

        if(payerWallet.typeEnum() == WalletType.LOJISTA)
            throw  new InvalidTransactionException("Lojista não pode enviar dinheiro");

        if (payerWallet.balance().compareTo(transaction.value()) < 0)
            throw  new InvalidTransactionException("Saldo Insuficiente");
    }
}
