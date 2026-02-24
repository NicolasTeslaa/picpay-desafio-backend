package com.picpay.desafio_picpay.authorization;

public record Authorization(
    String status,
    AuthorizationData data
) {
    public record AuthorizationData(boolean authorization) {}

    public boolean isAuthorized() {
        return data != null && data.authorization();
    }
}
