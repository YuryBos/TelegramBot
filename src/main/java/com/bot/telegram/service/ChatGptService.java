package com.bot.telegram.service;

import com.bot.telegram.model.CompletionGptRequest;
import com.bot.telegram.model.CompletionGptResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
/**
 * This class is responsible for sending requests to OpenAI API.
 * It uses CompletionGptRequest and CompletionGptResponse classes to send and receive data.
 * It uses OpenAiApiClient to send requests.
 * It uses TextConstantsService to get constants from the database.
 */

@Service
@RequiredArgsConstructor
public class ChatGptService {
    private final ObjectMapper jsonMapper;
    private final OpenAiApiClient client;

    public String chatWithGpt3(String message) throws Exception {
        var completion = CompletionGptRequest.defaultWith(TextConstantsService.getConstant("gpt_answer") + message);
        var postBodyJson = jsonMapper.writeValueAsString(completion);
        var responseBody = client.postToOpenAiApi(postBodyJson);
        var completionResponse = jsonMapper.readValue(responseBody, CompletionGptResponse.class);
        return completionResponse.firstAnswer().orElseThrow();
    }
}