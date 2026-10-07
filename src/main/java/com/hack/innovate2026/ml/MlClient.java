package com.hack.innovate2026.ml;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class MlClient {

    private final RestClient.Builder restClientBuilder;

    @Value("\${ml.service.url}")
    private String mlServiceUrl;

    public JsonNode analyze(MlAnalysisRequest request) {
        RestClient client = restClientBuilder
                .baseUrl(mlServiceUrl)
                .build();

        try {
            return client.post()
                    .uri("/ap/analyze")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(JsonNode.class);
        } catch (HttpStatusCodeException ex) {
            String detail = ex.getResponseBodyAsString();
            if (detail == null || detail.isBlank()) {
                detail = "ML service returned HTTP " + ex.getStatusCode().value();
            }
            throw new MlServiceException(
                    ex.getStatusCode().value(),
                    "ML service rejected the request: " + detail
            );
        } catch (Exception ex) {
            throw new MlServiceException(
                    502,
                    "ML service is unavailable: " + ex.getMessage()
            );
        }
    }
}
