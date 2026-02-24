package com.picpay.desafio_picpay.controller;

import com.picpay.desafio_picpay.transaction.Transaction;
import com.picpay.desafio_picpay.transaction.TransactionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("transaction")
public class TransactionController {
    private final TransactionService _transactionService;

    public TransactionController(TransactionService transactionService) {
        _transactionService = transactionService;
    }

    @PostMapping
    public Transaction createTransaction(@RequestBody Transaction transaction) {
        return _transactionService.create(transaction);
    }

    @GetMapping
    public List<Transaction> getTransaction() {
        return _transactionService.getTransactions();
    }
}
