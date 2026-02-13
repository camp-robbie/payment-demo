package com.bootcamp.paymentdemo.support.base;

import com.bootcamp.paymentdemo.support.helper.JwtTestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.client.RestClient;

@Tag("e2e")
public abstract class BaseE2ETest extends BaseIntegrationTest {

    @LocalServerPort
    protected int port;

    @Autowired
    protected JwtTestHelper jwtTestHelper;

    protected RestClient restClient;

    @BeforeEach
    void setUpRestClient() {
        restClient = RestClient.builder()
            .baseUrl("http://localhost:" + port)
            .build();
    }

    protected RestClient authenticatedClient(String email) {
        return RestClient.builder()
            .baseUrl("http://localhost:" + port)
            .defaultHeader("Authorization", jwtTestHelper.bearerToken(email))
            .build();
    }
}
