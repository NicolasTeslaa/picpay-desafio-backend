package com.picpay.desafio_picpay.wallet;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Table("WALLETS")
public record Wallet(
    @Id Long id,
    String fullName,
    Long cpf,
    String email,
    String password,
    Integer type,
    BigDecimal balance
) {
    public WalletType typeEnum() {
        return type == null ? null : (type == 1 ? WalletType.COMUN : WalletType.LOJISTA);
    }

    public Wallet debit(BigDecimal value) {
        return new Wallet(id, fullName, cpf, email, password, type, balance.subtract(value));
    }

    public Wallet credit(BigDecimal value) {
        return new Wallet(id, fullName, cpf, email, password, type, balance.add(value));
    }
}
