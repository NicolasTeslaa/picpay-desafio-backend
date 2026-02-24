package com.picpay.desafio_picpay.authorization;

import com.picpay.desafio_picpay.transaction.Transaction;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class AuthorizerService {
    private RestClient _restClient;

    public AuthorizerService(RestClient.Builder builder) {
        _restClient = builder
        .baseUrl("https://util.devi.tools/api/v2/authorize")
        .build();

    }

    public void authorize(Transaction transaction) {
       var response = _restClient.get()
            .retrieve()
            .toEntity(Authorization.class);

        if(response.getStatusCode().isError() || !response.getBody().isAuthorized()){
            throw new UnauthorizedTransactionException("Unauthorized Transaction");
        }
    }
}
