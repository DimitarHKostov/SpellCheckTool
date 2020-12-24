package bg.sofia.uni.fmi.mjt.spellchecker.analyzer;

import bg.sofia.uni.fmi.mjt.spellchecker.Metadata;
import bg.sofia.uni.fmi.mjt.spellchecker.analyzer.meaningfullists.Dictionary;
import bg.sofia.uni.fmi.mjt.spellchecker.analyzer.meaningfullists.StopWords;
import bg.sofia.uni.fmi.mjt.spellchecker.validator.Validator;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class TextAnalyzer {
    private final LinesAnalyzer linesAnalyzer;
    private static final int FIRST_LINE_NUMBER = 1;
    private int currentLineNumber = FIRST_LINE_NUMBER;

    public TextAnalyzer(Dictionary dictionary, StopWords stopWords, Set<Character> metatextSymbols) {
        this.linesAnalyzer = new LinesAnalyzer(dictionary, stopWords, metatextSymbols);
    }

    public void analyzeLine(String line) {
        Validator.validateNotNull(line, "line");

        this.linesAnalyzer.analyze(currentLineNumber, line);
        this.currentLineNumber++;
    }

    public Metadata collectMetadata() {
        return this.linesAnalyzer.extractCollectedMetadata();
    }

    public Map<Integer, List<String>> getProblemLines() {
        return this.linesAnalyzer.extractProblemLines();
    }

    public boolean foundProblemLines() {
        return this.getProblemLines().size() > 0;
    }
}