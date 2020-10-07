package ru.digitalhabbits.homework1.plugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FrequencyDictionaryPlugin
        implements PluginInterface {

    @Nullable
    @Override
    public String apply(@Nonnull String text) {
        text = text.replaceAll("\\\\n", "\n").toLowerCase();
        final Map<String, Integer> words = new HashMap<>();

        final Matcher wordMatcher = Pattern.compile("(\\b[a-zA-Z][a-zA-Z.0-9]*\\b)").matcher(text);
        wordMatcher.results()
                .map(MatchResult::group)
                .forEach(word -> words.merge(word, 1, Integer::sum));

        return words.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue() - e1.getValue())
                .map(e -> String.format("%s %d%n", e.getKey(), e.getValue()))
                .reduce("", (acc, s) -> acc + s);
    }
}