package com.bot.web.controller;

import com.bot.telegram.model.ChatGptMessageInput;
import com.bot.telegram.service.ChatGptService;
import com.bot.telegram.startup.TelegramBotService;
import com.bot.telegram.web.controller.ChatGptController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ChatGptControllerTest {

    @Mock
    private ChatGptService chatGptService;

    @Mock
    private TelegramBotService telegramBotService;

    @InjectMocks
    private ChatGptController controller;

    @Test
    public void testChatSuccess() throws Exception {
        // Arrange
        ChatGptMessageInput input = new ChatGptMessageInput();

        when(chatGptService.chatWithGpt3(input.getPrompt())).thenReturn("GPT3 Response");

        // Act
        controller.chat(input);

        // Assert
        verify(chatGptService, times(1)).chatWithGpt3(input.getPrompt());
        verify(telegramBotService, times(1)).sendTelegramMessage(input.getChatId(), "GPT3 Response");
    }
}
