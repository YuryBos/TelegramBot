package com.bot.telegram.service;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Slf4j
public class TextConstantsService {
    private static final Properties properties = new Properties();
    private static final Map<String, String> constantsMap = new HashMap<>();

    static {
        loadConstants("en");
    }

    public static void loadConstants(String language) {
        InputStream inputStream = null;
        try {
            String fileName = "constants_" + language + ".properties";
            inputStream = TextConstantsService.class.getResourceAsStream("/static/" + fileName);

            if (inputStream != null) {
                InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                properties.load(reader);

                for (String key : properties.stringPropertyNames()) {
                    constantsMap.put(key, properties.getProperty(key));
                }
            } else {
                log.error("Error: Unable to load resource {}", fileName);
            }
        } catch (IOException e) {
            log.error("Error: {}", e.getMessage());
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error("Error closing input stream: {}", e.getMessage());
                }
            }
        }
    }

    public static String getConstant(String constantName) {
        return constantsMap.get(constantName);
    }
}
