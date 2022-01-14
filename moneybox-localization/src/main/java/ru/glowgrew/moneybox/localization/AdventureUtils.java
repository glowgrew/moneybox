package ru.glowgrew.moneybox.localization;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.translation.GlobalTranslator;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public final class AdventureUtils {

    public static final @NotNull GsonComponentSerializer HEX_SECTION_COMPONENT_SERIALIZER =
            GsonComponentSerializer.gson();

    public static final @NotNull GsonComponentSerializer HEX_AMPERSAND_COMPONENT_SERIALIZER =
            GsonComponentSerializer.gson();

    private AdventureUtils() {
    }

    public static Component fromString(String text) {
        if (text.contains("&")) {
            return HEX_AMPERSAND_COMPONENT_SERIALIZER.deserialize(text);
        }
        return HEX_SECTION_COMPONENT_SERIALIZER.deserialize(text);
    }

    public static String toString(Component component) {
        return toString(component, false);
    }

    public static String toString(Component component, boolean useAmpersand) {
        if (useAmpersand) {
            return HEX_AMPERSAND_COMPONENT_SERIALIZER.serialize(component);
        }
        return HEX_SECTION_COMPONENT_SERIALIZER.serialize(component);
    }

    public static Component removeItalic(Component component) {
        return component.decoration(TextDecoration.ITALIC, false);
    }

    public static Component translate(String translationKey, Locale locale) {
        return translate(Component.translatable(translationKey), locale);
    }

    public static Component translate(TranslatableComponent component, Locale locale) {
        return GlobalTranslator.render(component, locale);
    }
}