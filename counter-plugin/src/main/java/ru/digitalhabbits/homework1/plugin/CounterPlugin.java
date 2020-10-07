package ru.digitalhabbits.homework1.plugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CounterPlugin
        implements PluginInterface {

    @Nullable
    @Override
    public String apply(@Nonnull String text) {
        final Matcher wordMatcher = Pattern.compile("(\\b[a-zA-Z][a-zA-Z.0-9]*\\b)").matcher(text);

        final long lines = text.lines().count();
        final long words = wordMatcher.results().count();;
        final long letters = text.toCharArray().length;

        return String.format("%d;%d;%d", lines, words, letters);
    }
}
