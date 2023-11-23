package com.bot.telegram.service;

import lombok.extern.slf4j.Slf4j;

import java.util.Random;

/**
 * Service for generating random emoji
 */
@Slf4j
public class RandomEmojiGenerator {

    public static String emojiToUnicode() {
        try {
            Random random = new Random();
            int randomNumber;
            do {
                randomNumber = random.nextInt(2041) + 127744;
            } while (!filterNumber(randomNumber));

            log.info("Generated random emoji number: {}", randomNumber);
            return "&#" + randomNumber + ";";

        } catch (Exception e) {
            log.error("Error generating random number: {}", e.getMessage());
            throw new RuntimeException("Error generating random number", e);
        }
    }

    private static boolean filterNumber(int number) {
        return (127744 <= number && number <= 127891) ||
                (127902 <= number && number <= 128317) ||
                (128394 <= number && number <= 128406) ||
                (128506 <= number && number <= 128591) ||
                (128640 <= number && number <= 128711) ||
                (129292 <= number && number <= 129535) ||
                (129648 <= number && number <= 129660) ||
                (129664 <= number && number <= 129672) ||
                (129680 <= number && number <= 129733) ||
                (129742 <= number && number <= 129755) ||
                (129760 <= number && number <= 129768) ||
                (129776 <= number && number <= 129784);
    }
}