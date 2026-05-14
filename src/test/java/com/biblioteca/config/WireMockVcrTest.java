package com.biblioteca.config;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class WireMockVcrTest {

    @RegisterExtension
    static WireMockExtension wireMock = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    @Test
    void deveSimularChamadaExternaComVcr() {
        // Exemplo de VCR: Mocking uma API de validação de ISBN
        wireMock.stubFor(get(urlEqualTo("/api/isbn/valida/123"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBody("{\"valido\": true}")));

        RestTemplate restTemplate = new RestTemplateBuilder().rootUri(wireMock.baseUrl()).build();
        
        ResponseEntity<String> response = restTemplate.getForEntity("/api/isbn/valida/123", String.class);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("{\"valido\": true}", response.getBody());
    }
}
