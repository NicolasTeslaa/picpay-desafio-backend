package com.picpay.desafio_picpay.authorization;

import com.picpay.desafio_picpay.transaction.Transaction;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Service
public class AuthorizerService {
    private final RestClient restClient;

    public AuthorizerService(RestClient.Builder builder) {
        this.restClient = builder
            .baseUrl("https://util.devi.tools/api/v2/authorize")
            .build();
    }

    public void authorize(Transaction transaction) {
        try {
            var response = restClient.get()
                .retrieve()
                .toEntity(Authorization.class);

            var body = response.getBody();
            if (body == null || !body.isAuthorized()) {
                throw new UnauthorizedTransactionException(
                    "Transação não autorizada pelo serviço de autorização."
                );
            }
        } catch (RestClientResponseException ex) {
            // 403 from this API means "authorization = false"
            if (ex.getStatusCode() == HttpStatus.FORBIDDEN) {
                throw new UnauthorizedTransactionException(
                    "Transação não autorizada pelo serviço de autorização."
                );
            }

            // any other 4xx/5xx: treat as external failure
            throw new UnauthorizedTransactionException(
                "Falha ao consultar o serviço de autorização (HTTP)."
            );
        }
    }
}
