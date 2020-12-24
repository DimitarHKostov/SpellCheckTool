package bg.sofia.uni.fmi.mjt.spellchecker.formatter;

import bg.sofia.uni.fmi.mjt.spellchecker.Metadata;
import bg.sofia.uni.fmi.mjt.spellchecker.validator.Validator;

import java.util.List;

public class LineFormatter {
    private static final String KEYWORD_METADATA_HEADER = "= = = Metadata = = =";
    private static final String KEYWORD_FINDINGS_HEADER = "= = = Findings = = =";
    private static final String KEYWORD_METADATA_CHARACTERS = " characters, ";
    private static final String KEYWORD_METADATA_WORDS = " words, ";
    private static final String KEYWORD_METADATA_ISSUES = " spelling issue(s) found";
    private static final String KEYWORD_LINE_NUMBER = "Line #";
    private static final String KEYWORD_WORD_OPENING_BRACKET = ", {";
    private static final String KEYWORD_WORD_CLOSING_BRACKET = "} - ";
    private static final String KEYWORD_SUGGESTIONS = "Possible suggestions are {";
    private static final String KEYWORD_CLOSING_BRACKET = "}";
    private static final String KEYWORD_COMMA = ",";
    private static final String KEYWORD_SPACE = " ";


    public String getMetadataHeader() {
        return System.lineSeparator() + KEYWORD_METADATA_HEADER + System.lineSeparator();
    }

    public String getFindingsHeader() {
        return System.lineSeparator() + KEYWORD_FINDINGS_HEADER;
    }

    public String formatMetadata(Metadata metadata) {
        Validator.validateNotNull(metadata, "metadata");
        int words = metadata.words();
        int characters = metadata.characters();
        int mistakes = metadata.mistakes();

        return characters + KEYWORD_METADATA_CHARACTERS
                + words + KEYWORD_METADATA_WORDS
                + mistakes + KEYWORD_METADATA_ISSUES;
    }

    public String formatLineWithSuggestions(String word, int lineNumber, List<String> words) {
        Validator.validateNotNull(words, "words");
        Validator.validatePositive(lineNumber);

        String line = System.lineSeparator()
                + KEYWORD_LINE_NUMBER + lineNumber
                + KEYWORD_WORD_OPENING_BRACKET + word + KEYWORD_WORD_CLOSING_BRACKET
                + KEYWORD_SUGGESTIONS;

        if (words.size() == 1) {
            return line + words.get(0) + KEYWORD_CLOSING_BRACKET;
        } else {
            StringBuilder builder = new StringBuilder();
            int addedWords = 0;
            while (addedWords < words.size() - 1) {
                builder.append(words.get(addedWords)).append(KEYWORD_COMMA).append(KEYWORD_SPACE);
                addedWords++;
            }

            builder.append(words.get(addedWords)).append(KEYWORD_CLOSING_BRACKET);
            return line + builder.toString();
        }
    }
}