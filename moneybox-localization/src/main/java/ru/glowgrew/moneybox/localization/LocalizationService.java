package ru.glowgrew.moneybox.localization;

import net.kyori.adventure.translation.TranslationRegistry;

import java.util.Arrays;
import java.util.Locale;

public interface LocalizationService {

    default void load(Locale... locale) {
        Arrays.stream(locale).forEach(this::load);
    }

    void load(Locale locale);

    void register();

    void unregister();

    TranslationRegistry registry();
}
