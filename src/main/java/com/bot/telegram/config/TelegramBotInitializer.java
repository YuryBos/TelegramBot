package com.bot.telegram.config;

import com.bot.telegram.startup.TelegramBotService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

/**
 * Clas to initialize the Telegram Bot
 */

@Slf4j
@Component
public class TelegramBotInitializer {
    @Autowired
    private TelegramBotService bot;

    @EventListener({ContextRefreshedEvent.class})
    public void init() {
        try {
            log.info("Initializing Telegram Bot...");
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(bot);
            log.info("Telegram Bot initialized successfully.");
        } catch (TelegramApiException e) {
            log.error("Error initializing Telegram Bot: {}", e.getMessage());
        }
    }
}