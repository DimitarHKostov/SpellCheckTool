package bg.sofia.uni.fmi.mjt.spellchecker;

import bg.sofia.uni.fmi.mjt.spellchecker.algorithm.AlgorithmPerformer;
import bg.sofia.uni.fmi.mjt.spellchecker.analyzer.TextAnalyzer;
import bg.sofia.uni.fmi.mjt.spellchecker.analyzer.meaningfullists.Dictionary;
import bg.sofia.uni.fmi.mjt.spellchecker.analyzer.meaningfullists.StopWords;
import bg.sofia.uni.fmi.mjt.spellchecker.formatter.LineFormatter;
import bg.sofia.uni.fmi.mjt.spellchecker.transformer.WordTransformer;
import bg.sofia.uni.fmi.mjt.spellchecker.validator.Validator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class NaiveSpellChecker implements SpellChecker {
    private static final String PATTERN_REGEX = ".*\\R|.+\\z";
    private static final Pattern PATTERN = Pattern.compile(PATTERN_REGEX);
    private static final char FIRST_ALPHABET_LETTER = 'a';
    private static final char LAST_ALPHABET_LETTER = 'z';
    private static final char FIRST_DIGIT = '0';
    private static final char LAST_DIGIT = '9';
    private final LineFormatter lineFormatter;
    private final AlgorithmPerformer algorithmPerformer;
    private final Set<Character> metatextSymbols;
    private final Dictionary dictionary;
    private final StopWords stopWords;

    public NaiveSpellChecker(Reader dictionaryReader, Reader stopwordsReader) {
        this.lineFormatter = new LineFormatter();
        this.metatextSymbols = new HashSet<>();

        Dictionary dictionary = new Dictionary(this.filterDictionaryWords(dictionaryReader));
        StopWords stopWords = new StopWords(this.filterStopWords(stopwordsReader));

        this.dictionary = dictionary;
        this.stopWords = stopWords;

        for (char letter = FIRST_ALPHABET_LETTER; letter <= LAST_ALPHABET_LETTER; letter++) {
            this.metatextSymbols.add(letter);
            this.metatextSymbols.add(Character.toUpperCase(letter));
        }

        for (char digit = FIRST_DIGIT; digit <= LAST_DIGIT; digit++) {
            this.metatextSymbols.add(digit);
        }

        this.updateMetatextSymbols(dictionary.dictionaryWords());
        this.updateMetatextSymbols(stopWords.stopWords());

        this.algorithmPerformer = new AlgorithmPerformer(dictionary);
    }

    @Override
    public void analyze(Reader textReader, Writer output, int suggestionsCount) {
        Validator.validateNotNull(textReader, "textReader");
        Validator.validateNotNull(output, "output");
        Validator.validatePositive(suggestionsCount);

        TextAnalyzer analyzer = new TextAnalyzer(this.dictionary, this.stopWords, this.metatextSymbols);

        this.commitToBeAnalyzed(analyzer, textReader, output, suggestionsCount);
    }

    @Override
    public Metadata metadata(Reader textReader) {
        Validator.validateNotNull(textReader, "textReader");

        TextAnalyzer analyzer = new TextAnalyzer(dictionary, stopWords, this.metatextSymbols);

        this.commitToTextAnalyzer(analyzer, textReader);
        return analyzer.collectMetadata();
    }

    @Override
    public List<String> findClosestWords(String word, int n) {
        Validator.validateNotNull(word, "word");
        Validator.validatePositive(n);

        this.algorithmPerformer.setSuggestionsCount(n);
        return this.algorithmPerformer.getClosestWords(word);
    }

    private String generateNextLine(Scanner scanner) {
        return scanner.findWithinHorizon(PATTERN, 0);
    }

    private void commitToTextAnalyzer(TextAnalyzer analyzer, Reader textReader) {
        try (var scanner = new Scanner(new BufferedReader(textReader))) {
            String currentLine;
            while ((currentLine = this.generateNextLine(scanner)) != null) {
                analyzer.analyzeLine(currentLine);
            }
        }
    }

    private void commitToBeAnalyzed(TextAnalyzer analyzer, Reader input, Writer output, int suggestionsCount) {
        try (var scanner = new Scanner(new BufferedReader(input))) {
            this.algorithmPerformer.setSuggestionsCount(suggestionsCount);
            String currentLine;

            while ((currentLine = this.generateNextLine(scanner)) != null) {
                output.append(currentLine).flush();
                analyzer.analyzeLine(currentLine);
            }

            this.appendCalculatedData(output, analyzer);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    private void appendCalculatedData(Writer output, TextAnalyzer analyzer) {
        Metadata metadata = analyzer.collectMetadata();

        this.appendMetadata(output, metadata, analyzer);
    }

    private void appendMetadata(Writer output, Metadata metadata, TextAnalyzer analyzer) {
        try {
            output.append(this.lineFormatter.getMetadataHeader()).flush();
            output.append(this.lineFormatter.formatMetadata(metadata)).flush();

            this.appendFindings(output, analyzer);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    private void appendFindings(Writer output, TextAnalyzer analyzer) {
        try {
            output.append(this.lineFormatter.getFindingsHeader()).flush();

            if (analyzer.foundProblemLines()) {
                this.appendSuggestions(output, analyzer);
            }
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    private void appendSuggestions(Writer output, TextAnalyzer analyzer) {
        try {
            for (Map.Entry<Integer, List<String>> currentLine : analyzer.getProblemLines().entrySet()) {
                for (String currentWrongWord : currentLine.getValue()) {
                    int lineNumber = currentLine.getKey();
                    List<String> suggestions = this.algorithmPerformer.getClosestWords(currentWrongWord);

                    String formattedLine =
                            this.lineFormatter.formatLineWithSuggestions(currentWrongWord, lineNumber, suggestions);

                    output.append(formattedLine).flush();
                }
            }
            output.close();
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    private List<String> filterDictionaryWords(Reader reader) {
        try (var input = new BufferedReader(reader)) {
            return input.lines()
                    .map(String::trim)
                    .filter(l -> l.length() > 1)
                    .map(WordTransformer::reform)
                    .filter(l -> !l.equals(""))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    private List<String> filterStopWords(Reader reader) {
        try (var input = new BufferedReader(reader)) {
            return input.lines()
                    .map(String::trim)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    private void updateMetatextSymbols(List<String> words) {
        for (String word : words) {
            for (int index = 0; index < word.length(); index++) {
                char symbol = word.charAt(index);
                if (Character.isLetter(symbol)) {
                    if (Character.isLowerCase(symbol)) {
                        this.metatextSymbols.add(Character.toUpperCase(symbol));
                    } else {
                        this.metatextSymbols.add(Character.toLowerCase(symbol));
                    }
                }
                this.metatextSymbols.add(symbol);
            }
        }
    }
}