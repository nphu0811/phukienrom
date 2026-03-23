package com.example.demo.util;
import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

public class SlugUtil {
    private static final Pattern NON_LATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]+");
    private static final Pattern EDGES_DASHES = Pattern.compile("(^-|-$)");

    public static String toSlug(String input) {
        if (input == null) return "";
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        normalized = normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        normalized = normalized
            .replace("đ", "d").replace("Đ", "d")
            .replace("ư", "u").replace("ơ", "o")
            .replace("ă", "a").replace("â", "a")
            .replace("ê", "e").replace("ô", "o");
        String slug = WHITESPACE.matcher(normalized.toLowerCase(Locale.ENGLISH)).replaceAll("-");
        slug = NON_LATIN.matcher(slug).replaceAll("");
        slug = EDGES_DASHES.matcher(slug).replaceAll("");
        return slug;
    }
}
