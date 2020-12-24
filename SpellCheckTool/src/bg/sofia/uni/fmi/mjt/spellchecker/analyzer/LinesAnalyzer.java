package bg.sofia.uni.fmi.mjt.spellchecker.analyzer;

import bg.sofia.uni.fmi.mjt.spellchecker.Metadata;
import bg.sofia.uni.fmi.mjt.spellchecker.analyzer.meaningfullists.Dictionary;
import bg.sofia.uni.fmi.mjt.spellchecker.analyzer.meaningfullists.StopWords;
import bg.sofia.uni.fmi.mjt.spellchecker.analyzer.metadata.MetadataContainer;
import bg.sofia.uni.fmi.mjt.spellchecker.enums.WordType;
import bg.sofia.uni.fmi.mjt.spellchecker.validator.Validator;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LinesAnalyzer {
    private static final String SPACE_REGEX = "\\s{2,}";
    private final Set<Character> metatextSymbols;
    private final WordsAnalyzer wordsAnalyzer;
    private final MetadataContainer metadataContainer;
    private Map<Integer, List<String>> problemLines;

    public LinesAnalyzer(Dictionary dictionary, StopWords stopWords, Set<Character> metatextSymbols) {
        this.wordsAnalyzer = new WordsAnalyzer(dictionary, stopWords);
        this.metadataContainer = new MetadataContainer();
        this.problemLines = new LinkedHashMap<>();
        this.metatextSymbols = metatextSymbols;
    }

    public Map<Integer, List<String>> extractProblemLines() {
        return this.problemLines;
    }

    public Metadata extractCollectedMetadata() {
        int characters = this.metadataContainer.getCharacters();
        int words = this.metadataContainer.getWords();
        int mistakes = this.metadataContainer.getMistakes();

        return new Metadata(characters, words, mistakes);
    }

    public void analyze(int lineNumber, String line) {
        Validator.validateNotNull(line, "line");
        Validator.validatePositive(lineNumber);

        line = this.reformLineSpaces(line);
        String[] rawWords = line.split(" ");

        for (String w : rawWords) {
            this.metadataContainer.addCharacters(w.length());
        }

        List<String> analyzableWords = this.extractPossibleWords(rawWords);

        for (String word : analyzableWords) {
            this.interfere(lineNumber, word, this.getWordType(word));
        }
    }

    private void interfere(int lineNumber, String word, WordType type) {
        if (type == WordType.WRONG_WORD) {
            if (this.problemLines.containsKey(lineNumber)) {
                this.problemLines.get(lineNumber).add(word);
            } else {
                List<String> wrongWords = new ArrayList<>();
                wrongWords.add(word);
                this.problemLines.put(lineNumber, wrongWords);
            }
            this.metadataContainer.addAnotherMistake();
        }

        if (type != WordType.STOP_WORD) {
            this.metadataContainer.addAnotherWord();
        }
    }


    private WordType getWordType(String word) {
        return this.wordsAnalyzer.getWordType(word);
    }

    private List<String> extractPossibleWords(String[] characterSequences) {
        List<String> actualWords = new LinkedList<>();
        int index = 0;
        int increment = 1;

        for (String currentSequence : characterSequences) {
            while (index < currentSequence.length()) {
                if (this.metatextSymbols.contains(currentSequence.charAt(index))) {
                    String word = this.extractWord(currentSequence.substring(index));

                    actualWords.add(word);
                    increment = word.length();
                }
                index += increment;
                increment = 1;
            }
            index = 0;
        }

        return actualWords;
    }

    private String extractWord(String word) {
        StringBuilder foundWord = new StringBuilder();
        int index = 0;

        while (index < word.length() && this.metatextSymbols.contains(word.charAt(index))) {
            foundWord.append(word.charAt(index));
            index++;
        }

        return foundWord.toString();
    }

    private String reformLineSpaces(String line) {
        return line.replaceAll(SPACE_REGEX, " ").trim();
    }
}
