package bg.sofia.uni.fmi.mjt.spellchecker.analyzer.metadata;

public class MetadataContainer {
    private int characters = 0;
    private int words = 0;
    private int mistakes = 0;

    public void addAnotherWord() {
        this.words++;
    }

    public void addAnotherMistake() {
        this.mistakes++;
    }

    public void addCharacters(int charNumber) {
        this.characters += charNumber;
    }

    public int getCharacters() {
        return this.characters;
    }

    public int getWords() {
        return this.words;
    }

    public int getMistakes() {
        return this.mistakes;
    }
}