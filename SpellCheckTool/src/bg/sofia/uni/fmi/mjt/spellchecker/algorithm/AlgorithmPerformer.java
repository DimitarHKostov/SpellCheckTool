package bg.sofia.uni.fmi.mjt.spellchecker.algorithm;

import bg.sofia.uni.fmi.mjt.spellchecker.analyzer.meaningfullists.Dictionary;
import bg.sofia.uni.fmi.mjt.spellchecker.validator.Validator;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AlgorithmPerformer {
    private final Map<String, Map<String, Integer>> wordSyllables;
    private final Map<String, Double> wordLengths;
    private int suggestionsCount;

    public AlgorithmPerformer(Dictionary dictionary) {
        this.wordSyllables =
                dictionary.dictionaryWords().stream()
                        .collect(Collectors.toMap(this::getIdentity, this::createSyllablesMap));

        this.wordLengths = this.setWordsLength(wordSyllables);
    }

    public void setSuggestionsCount(int n) {
        Validator.validatePositive(n);
        this.suggestionsCount = n;
    }

    public List<String> getClosestWords(String word) {
        Validator.validateNotNull(word, "word");
        word = word.toLowerCase();
        Map<String, Integer> inputWordSyllables = this.createSyllablesMap(word);

        Map<String, Double> similarityTable = new HashMap<>();

        this.wordSyllables
                .forEach((key, value) -> similarityTable.put(key, this.calculateSimilarity(inputWordSyllables, key)));

        return similarityTable.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(this.suggestionsCount)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

    }

    private double calculateSimilarity(Map<String, Integer> inputWordSyllables, String currentDictionaryWord) {
        double inputWordLength = this.calculateLength(inputWordSyllables);
        double dictionaryWordLength = this.wordLengths.get(currentDictionaryWord);

        Map<String, Integer> dictionaryWordSyllables = this.wordSyllables.get(currentDictionaryWord);

        Map<String, Integer> filteredDictionaryWordSyllables =
                dictionaryWordSyllables.entrySet()
                        .stream()
                        .filter(entry -> inputWordSyllables.containsKey(entry.getKey()))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        int vectorProduct = inputWordSyllables.entrySet()
                .stream()
                .filter(entry -> filteredDictionaryWordSyllables.containsKey(entry.getKey()))
                .reduce(0, (total, currPair) ->
                                total += (currPair.getValue() * filteredDictionaryWordSyllables.get(currPair.getKey())),
                        Integer::sum);

        return (double) vectorProduct / (inputWordLength * dictionaryWordLength);
    }

    private Map<String, Integer> createSyllablesMap(String word) {
        Map<String, Integer> syllablesCount = new HashMap<>();
        String currentSyllable;
        StringBuilder builder = new StringBuilder();
        int index = 0;

        while (index < word.length() - 1) {
            builder.append(this.changeCaseIfNeeded(word.charAt(index)))
                    .append(this.changeCaseIfNeeded(word.charAt(index + 1)));
            currentSyllable = builder.toString();
            if (syllablesCount.containsKey(currentSyllable)) {
                int oldValue = syllablesCount.get(currentSyllable);
                syllablesCount.replace(currentSyllable, oldValue + 1);
            } else {
                syllablesCount.put(currentSyllable, 1);
            }
            builder.setLength(0);
            index++;
        }

        return syllablesCount;
    }

    private char changeCaseIfNeeded(char symbol) {
        return Character.isLetter(symbol) && Character.isUpperCase(symbol)
                ? Character.toLowerCase(symbol) : symbol;
    }

    private double calculateLength(Map<String, Integer> syllablesCount) {
        int res = syllablesCount.values().stream()
                .reduce(0, (total, element) -> total + (int) Math.pow(element, 2));

        return Math.sqrt(res);
    }

    private Map<String, Double> setWordsLength(Map<String, Map<String, Integer>> wordSyllables) {
        Map<String, Double> lengths = new HashMap<>();

        wordSyllables.forEach((key, value) -> lengths.put(key, this.calculateLength(value)));

        return lengths;
    }

    private String getIdentity(String word) {
        return word;
    }

}
