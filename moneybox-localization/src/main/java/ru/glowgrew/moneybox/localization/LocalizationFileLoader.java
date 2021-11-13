package ru.glowgrew.moneybox.localization;

import java.io.InputStream;

@FunctionalInterface
public interface LocalizationFileLoader {

    InputStream load(String filename);
}
