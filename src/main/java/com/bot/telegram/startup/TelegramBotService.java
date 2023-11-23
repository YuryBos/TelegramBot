package com.bot.telegram.startup;

import com.bot.telegram.config.TelegramBotConfig;
import com.bot.telegram.service.ChatGptService;
import com.bot.telegram.service.RandomEmojiGenerator;
import com.bot.telegram.service.TextConstantsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

@Component
@RequiredArgsConstructor
@Slf4j
public class TelegramBotService extends TelegramLongPollingBot {

    private final TelegramBotConfig config;
    private final ChatGptService chatGptService;
    private final UnaryOperator<String> text = TextConstantsService::getConstant;
    private Map<Long, BotState> userStates = new HashMap<>();

    private enum BotState {
        START,
        REPORT_MENU
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            long chatId = update.getMessage().getChatId();
            SendMessage message = new SendMessage();
            message.setChatId(String.valueOf(chatId));
            message.setParseMode("HTML");

            BotState currentState = userStates.getOrDefault(chatId, BotState.START);

            switch (currentState) {
                case START -> handleStartState(update, message);
                case REPORT_MENU -> handleReportMenuState(update, message);
            }
        }
    }

    private void handleStartState(Update update, SendMessage message) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            long chatId = update.getMessage().getChatId();
            SendMessage sendTelegramMessage = new SendMessage();
            sendTelegramMessage.setChatId(String.valueOf(chatId));

            switch (update.getMessage().getText()) {
                case "/start", "старт", "start" -> sendStartMenu(sendTelegramMessage);
                case "Report", "Bericht", "Звіт", "Отчет" -> {
                    userStates.put(update.getMessage().getChatId(), BotState.REPORT_MENU);
                    sendReportMenu(sendTelegramMessage);
                }
                case "En", "De", "Uk", "Ru" -> {
                    TextConstantsService.loadConstants(update.getMessage().getText().toUpperCase());
                    sendStartMenu(sendTelegramMessage);
                }
                case "☺" -> sendTelegramMessage(chatId, RandomEmojiGenerator.emojiToUnicode());

                default -> sendGptResponse(chatId, update.getMessage().getText());
            }
        }
    }

    private void handleReportMenuState(Update update, SendMessage message) {
        long chatId = update.getMessage().getChatId();

        switch (update.getMessage().getText()) {
            case "back", "zurück", "назад" -> {
                userStates.put(update.getMessage().getChatId(), BotState.START);
                sendStartMenu(message);
            }
            case "Report", "Bericht", "Звіт", "Отчет" ->
                    sendTelegramMessage(chatId, text.apply("menu.report_description"));

            case "create", "erstellen", "створити", "создать" -> {
                createFileForUser(update.getMessage().getFrom().getUserName());}
            case "clear", "löschen", "очистити", "очистить" -> {
            }
            case "view", "ansehen", "посмотреть", "подивитися" -> {
            }
            case "mark", "markieren", "отметить", "відзначити" -> {
            }
            default -> sendGptResponse(chatId, update.getMessage().getText());
        }
    }


    private void sendStartMenu(SendMessage message) {
        ReplyKeyboardMarkup replyMarkup = new ReplyKeyboardMarkup();
        replyMarkup.setSelective(true);
        replyMarkup.setResizeKeyboard(true);
        replyMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("start"));
        row1.add(new KeyboardButton(text.apply("button.report")));

        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton("En"));
        row2.add(new KeyboardButton("De"));
        row2.add(new KeyboardButton("Uk"));
        row2.add(new KeyboardButton("Ru"));
        row2.add(new KeyboardButton("☺"));

        keyboard.add(row1);
        keyboard.add(row2);
        replyMarkup.setKeyboard(keyboard);

        message.setText(text.apply("start_message"));
        message.setReplyMarkup(replyMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error: {}", e.getMessage());
        }
    }

    private void sendReportMenu(SendMessage message) {
        ReplyKeyboardMarkup replyMarkup = new ReplyKeyboardMarkup();
        replyMarkup.setSelective(true);
        replyMarkup.setResizeKeyboard(true);
        replyMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton(text.apply("button.back")));
        row1.add(new KeyboardButton(text.apply("button.report")));

        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton(text.apply("button.create")));
        row2.add(new KeyboardButton(text.apply("button.clear")));
        row2.add(new KeyboardButton(text.apply("button.view")));
        row2.add(new KeyboardButton(text.apply("button.mark")));

        keyboard.add(row1);
        keyboard.add(row2);
        replyMarkup.setKeyboard(keyboard);

        message.setText(text.apply("menu.report_start"));
        message.setReplyMarkup(replyMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error: {}", e.getMessage());
        }
    }

    private void startCommandReceived(long chatId, String name) {
        String answer = text.apply("greeting.message") + name + " ))";
        sendTelegramMessage(chatId, answer);
        log.info("Replied to user " + name);
    }

    private void sendGptResponse(long chatId, String userMessage) {
        try {
            String gptResponse = chatGptService.chatWithGpt3(userMessage);
            sendTelegramMessage(chatId, gptResponse);
        } catch (Exception e) {
            log.error("Error communicating with GPT3 API: " + e.getMessage());
            sendTelegramMessage(chatId, text.apply("default.message"));
        }
    }

    public void sendTelegramMessage(long chatId, String textToSendHtml) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSendHtml);
        message.setParseMode("HTML");

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error:" + e.getMessage());
        }
    }
    private void createFileForUser(String username) {
        String directoryPath = "src/main/resources/users/";
        String fileName = directoryPath + username + ".txt";

        try {
            Files.createDirectories(Path.of(directoryPath));

            Path filePath = Files.createFile(Path.of(fileName));

            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = now.format(formatter);

            Files.writeString(filePath, formattedDateTime, StandardOpenOption.WRITE);

            log.info("File created successfully for user: {}", username);
        } catch (IOException e) {
            log.error("Error creating file for user {}: {}", username, e.getMessage());
        }
    }
}
