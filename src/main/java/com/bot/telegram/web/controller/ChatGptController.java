package com.bot.telegram.web.controller;

import com.bot.telegram.model.ChatGptMessageInput;
import com.bot.telegram.service.ChatGptService;
import com.bot.telegram.startup.TelegramBotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
/**
 * This controller is responsible for receiving messages from the Telegram bot and sending them to the GPT-3 API.
 *
 * @see com.bot.telegram.startup.TelegramBotService
 * @see com.bot.telegram.service.ChatGptService
 */

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatGptController {

    private final ChatGptService chatGptService;
    private final TelegramBotService telegramBotService;

    @PostMapping(path = "/")
    public void chat(@RequestBody ChatGptMessageInput dto) {
        try {
            log.info("Received message: {}", dto.getPrompt());

            String gpt3Response = chatGptService.chatWithGpt3(dto.getPrompt());

            log.info("GPT-3 Response: {}", gpt3Response);

            telegramBotService.sendTelegramMessage(dto.getChatId(), gpt3Response);
        } catch (Exception e) {
            log.error("Error in communication with GPT-3 API: {}", e.getMessage());
        }
    }
}

