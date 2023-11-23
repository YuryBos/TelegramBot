package com.bot.telegram.model;
/**
 * This class is used to store the input message from the user.
 * It is used in the ChatGptService class.
 */

public record CompletionGptRequest(String model, String prompt,
                                   double temperature, int max_tokens) {

    public static CompletionGptRequest defaultWith(String prompt) {
        return new CompletionGptRequest("text-davinci-003", prompt, 0.7, 1000);
    }
}