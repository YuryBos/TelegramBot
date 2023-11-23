package com.bot.telegram.model;

import lombok.Data;
/**
 * This class is used to store the input message from the user.
 * It is used in the ChatGptService class.
 */
@Data

public class ChatGptMessageInput {
    private String prompt;

    public long getChatId() {
        return 0;
    }
}