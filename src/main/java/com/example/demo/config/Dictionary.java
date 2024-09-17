package com.example.demo.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

@Slf4j
@Component
public class Dictionary {

    private static ResourceBundle lines;

    public static String getString(String name) {
        try {
            return lines.getString(name);
        } catch (MissingResourceException e) {
            log.error("!_" + name + "_!", e);
            return "!_" + name + "_!";
        }
    }

    @Autowired
    private void updateLocale(@Value("${app.lang}") String lang) {
        lines = ResourceBundle.getBundle("lines", new Locale(lang));
    }

}
