package com.bot.telegram.model;

import java.util.List;
import java.util.Optional;
/**
 * This class is used to store the response from the OpenAI ChatGPT API.
 * It is used in the ChatGptService class.
 */

public record CompletionGptResponse(Usage usage, List<Choice> choices) {

    public Optional<String> firstAnswer() {
        if (choices == null || choices.isEmpty())
            return Optional.empty();
        return Optional.of(choices.get(0).text);
    }

    record Usage(int total_tokens, int prompt_tokens, int completion_tokens) {}

    record Choice(String text) {}
}