package bg.sofia.uni.fmi.mjt.spellchecker.transformer;

import bg.sofia.uni.fmi.mjt.spellchecker.validator.Validator;

public class WordTransformer {
    public static String reform(String word) {
        Validator.validateNotNull(word, "word");

        return trimNonAlphanumeric(word);
    }

    private static String trimNonAlphanumeric(String word) {
        int startIndex = 0;
        int endIndex = word.length() - 1;

        while (!isAlphanumeric(word.charAt(startIndex))) {
            startIndex++;
        }

        if (startIndex < word.length() - 1) {
            while (!isAlphanumeric(word.charAt(endIndex))) {
                endIndex--;
            }
        }

        return word.substring(startIndex, endIndex + 1);
    }

    private static boolean isAlphanumeric(char c) {
        return Character.isLetter(c) || Character.isDigit(c);
    }
}
