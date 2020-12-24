package bg.sofia.uni.fmi.mjt.spellchecker.analyzer;

import bg.sofia.uni.fmi.mjt.spellchecker.analyzer.meaningfullists.Dictionary;
import bg.sofia.uni.fmi.mjt.spellchecker.analyzer.meaningfullists.StopWords;
import bg.sofia.uni.fmi.mjt.spellchecker.enums.WordType;
import bg.sofia.uni.fmi.mjt.spellchecker.validator.Validator;

public class WordsAnalyzer {
    private final Dictionary dictionary;
    private final StopWords stopWords;

    public WordsAnalyzer(Dictionary dictionary, StopWords stopWords) {
        this.dictionary = dictionary;
        this.stopWords = stopWords;
    }

    public WordType getWordType(String word) {
        Validator.validateNotNull(word, "word");

        boolean isDictionaryWord =
                this.dictionary.dictionaryWords().stream()
                        .anyMatch(l -> word.compareToIgnoreCase(l) == 0);

        if (isDictionaryWord) {
            return WordType.DICTIONARY_WORD;
        }

        boolean isStopWord =
                this.stopWords.stopWords().stream()
                        .anyMatch(l -> word.compareToIgnoreCase(l) == 0);

        if (isStopWord) {
            return WordType.STOP_WORD;
        }

        return WordType.WRONG_WORD;
    }
}
