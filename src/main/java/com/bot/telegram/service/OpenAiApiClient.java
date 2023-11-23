package com.bot.telegram.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
/**
 * Class for sending requests to OpenAI API
 */
@Slf4j
@Component
public class OpenAiApiClient {

    @Value("${openai.api_key}")
    private String openaiApiKey;
    @Value("${openai.api_uri}")
    private String openaiApiUri;

    private final HttpClient client = HttpClient.newHttpClient();

    public String postToOpenAiApi(String requestBodyAsJson) {
        try {
            log.info("Sending request to OpenAI API...");
            var request = HttpRequest.newBuilder().uri(URI.create(openaiApiUri))
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + openaiApiKey)
                    .POST(BodyPublishers.ofString(requestBodyAsJson)).build();
            return client.send(request, HttpResponse.BodyHandlers.ofString()).body();
        } catch (IOException | InterruptedException e) {
            log.error("Error communicating with OpenAI API: {}", e.getMessage());
            throw new RuntimeException("Error communicating with OpenAI API", e);
        }
    }
}