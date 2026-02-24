package com.picpay.desafio_picpay.transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("TRANSACTIONS")
public record Transaction(
    @Id Long id,
    @Column("PAYER")
    Long payerId,
    @Column("PAYEE")
    Long payeeId,
    BigDecimal value,
    @CreatedDate LocalDateTime createdAt){
    public Transaction{
        // seta duas casas decimais para a coluna value
        value = value.setScale(2);
    }
}
