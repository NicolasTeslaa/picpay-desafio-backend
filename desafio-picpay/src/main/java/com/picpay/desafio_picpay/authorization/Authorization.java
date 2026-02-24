package com.picpay.desafio_picpay.authorization;

import com.picpay.desafio_picpay.transaction.Transaction;

import javax.xml.crypto.Data;

public record Authorization(
    String status,
    Data data)
{
    public record Data(boolean authorization){}

    public boolean isAuthorized() {
        return data.authorization();
    }
}
