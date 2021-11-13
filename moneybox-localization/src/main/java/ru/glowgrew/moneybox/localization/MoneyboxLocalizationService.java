package ru.glowgrew.moneybox.localization;

import dev.akkinoc.util.YamlResourceBundle;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.TranslationRegistry;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public final class MoneyboxLocalizationService implements LocalizationService {

    private static final Logger log = Logger.getLogger("Minecraft");

    private final TranslationRegistry registry;
    private final LocalizationFileLoader fileLoader;

    private MoneyboxLocalizationService(Key name, Locale defaultLocale, LocalizationFileLoader fileLoader) {
        this.registry = TranslationRegistry.create(name);
        this.fileLoader = fileLoader;

        registry.defaultLocale(defaultLocale);
        load(defaultLocale); // we should force load the default locale to use it as a backup language
    }

    public static MoneyboxLocalizationService create(
            Key name, Locale defaultLocale, LocalizationFileLoader fileLoader
    ) {
        return new MoneyboxLocalizationService(name, defaultLocale, fileLoader);
    }

    @Override
    public void load(Locale locale) {
        final String language = locale.getLanguage();
        final String filename = "localization/" + language + ".yml";
        log.info("Loading " + language + " language from file " + filename);
        try (InputStream inputStream = fileLoader.load(filename)) {
            if (inputStream == null) {
                log.warning("A language file " + filename + " for " + language + " locale not found.");
                return;
            }
            ResourceBundle resourceBundle;
            try (final InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
                resourceBundle = new YamlResourceBundle(reader);
            }
            registry.registerAll(locale, resourceBundle, true);
            log.info("Loaded a total of " +
                     resourceBundle.keySet().size() +
                     " translations for " +
                     language +
                     " language.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void register() {
        GlobalTranslator.get().addSource(registry);
        log.info("Translations were hooked into Adventure API");
    }

    @Override
    public void unregister() {
        GlobalTranslator.get().removeSource(registry);
        log.info("Translations were unhooked into Adventure API");
    }

    @Override
    public TranslationRegistry registry() {
        return registry;
    }
}