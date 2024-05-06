package sample;

import java.util.regex.Pattern;

public class BadWordsFilter {
    private static final Pattern BAD_WORD_PATTERN = Pattern.compile("\\b(shit|shitup|test)\\b", Pattern.CASE_INSENSITIVE);

    public static String filterBadWords(String input) {
        return BAD_WORD_PATTERN.matcher(input).replaceAll(match -> "*".repeat(match.group().length()));
    }
}