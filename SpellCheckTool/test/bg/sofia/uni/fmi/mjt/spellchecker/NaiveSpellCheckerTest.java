package bg.sofia.uni.fmi.mjt.spellchecker;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class NaiveSpellCheckerTest {
    private static SpellChecker spellChecker;

    private static final Reader dictionaryReader = new StringReader(String.join(System.lineSeparator(),
            List.of("asap", "asda", "asdas", "aaaa",
                    "bee", "brother", "..&bromance", "breed",
                    "cat", "coffee", "capybara", "crossover", "chello",
                    "dog", "&^%domain&*(", "drive", "drunk",
                    "eagle", "east", "eat", "emergency",
                    "frog", "fill", "fast", "funky",
                    "great", "gross", "grave", "gravity",
                    "hive", "huge", "..hello&*&", "height",
                    "ice", "...idea", "ignore", "illegal;;'",
                    "jaw", "job", "jelly", "jet",
                    "key", "kid", "kill", "kickoff",
                    "label", "lamp", "land", "last",
                    "map", "mother", "mail", "main",
                    "name", "natural", "@#$%neck", "native.,.,",
                    "olive", "$%ocean", "occupy", "offend",
                    "pain", "%.%<panic", "palm", "paper",
                    "quick", "queen", "quote", "quit",
                    "race", "rain", "rapidYB*(", "rare",
                    "sad", "sale", "same", "sandwich",
                    "thread", "taxi", "table", "target",
                    "unit", "umbrella", "uniform", "unique",
                    "value", "vehicle", "verb", "view",
                    "walk", "wallet", "waste", "wax",
                    "xylidin", "xerosis", "xenolith", "xenogeny",
                    "yesterday", "yoga", "yeah", "youngster",
                    "zoo", "zone", "zebra", "zinc")));

    private static final Reader stopWordReader = new StringReader(String.join(System.lineSeparator(),
            List.of("am", "i", "the", "a", "an", "of", "up", "out", "do", "by", "as")));

    @BeforeClass
    public static void initializeChecker() {
        spellChecker = new NaiveSpellChecker(dictionaryReader, stopWordReader);
    }

    @AfterClass
    public static void clear() throws IOException {
        dictionaryReader.close();
        stopWordReader.close();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMetadataExpectedIllegalArgumentException() {
        spellChecker.metadata(null);
    }

    @Test
    public void testMetadataNotNull() {
        Reader dogTextReader = new StringReader("helllo, i am a dog!");
        Metadata actual = spellChecker.metadata(dogTextReader);
        assertNotNull("metadata method shouldn`t return null", actual);
    }

    @Test
    public void testMetadataCorrectResultOneLine() {
        Reader dogTextReader = new StringReader("helllo, i am a dog!");
        Metadata actual = spellChecker.metadata(dogTextReader);
        Metadata expected = new Metadata(15, 2, 1);

        assertEquals("metadata result should be exactly the same", expected, actual);
    }

    @Test
    public void testMetadataWrongCharactersCountOneLine() {
        Reader dogTextReader = new StringReader("helllo, i am a dog!");
        Metadata actual = spellChecker.metadata(dogTextReader);
        Metadata expected = new Metadata(16, 2, 1);

        assertNotEquals("results should differ, wrong # of characters", expected, actual);
    }

    @Test
    public void testMetadataWrongWordsCountOneLine() {
        Reader dogTextReader = new StringReader("helllo, i am a dog!");
        Metadata actual = spellChecker.metadata(dogTextReader);
        Metadata expected = new Metadata(15, 10, 1);

        assertNotEquals("results should differ, wrong # of words", expected, actual);
    }

    @Test
    public void testMetadataWrongMistakesCountOneLine() {
        Reader dogTextReader = new StringReader("helllo, i am a dog!");
        Metadata actual = spellChecker.metadata(dogTextReader);
        Metadata expected = new Metadata(15, 2, 4);

        assertNotEquals("results should differ, wrong # of mistakes", expected, actual);
    }

    @Test
    public void testMetadataCorrectResultMultipleLines() {
        Reader reader = new StringReader(String.join(System.lineSeparator(),
                List.of("asdasd&&&^ 1pesho^%$#@ 1pesho",
                        "&*gosho.. the,1PeShO,oooooaOO1pzab.asDa",
                        "()*asddas-, ivan (&&&&&&&)(*******)..",
                        "helllo, i am a cat!")));
        Metadata actual = spellChecker.metadata(reader);
        Metadata expected = new Metadata(115, 11, 9);

        assertEquals("expected exactly the same metadata when multiple lines", expected, actual);
    }

    @Test
    public void testMetadataWrongCharactersMultipleLines() {
        Reader reader = new StringReader(String.join(System.lineSeparator(),
                List.of("asdasd&&&^ 1pesho^%$#@ 1pesho",
                        "&*gosho.. the,1PeShO,oooooaOO1pzab.asDa",
                        "()*asddas-, ivan (&&&&&&&)(*******)..",
                        "helllo, i am a cat!")));
        Metadata actual = spellChecker.metadata(reader);
        Metadata expected = new Metadata(114, 12, 10);

        assertNotEquals("expected not equal result", expected, actual);
    }

    @Test
    public void testMetadataWrongWordsCountMultipleLines() {
        Reader reader = new StringReader(String.join(System.lineSeparator(),
                List.of("asdasd&&&^ 1pesho^%$#@ 1pesho",
                        "&*gosho.. the,1PeShO,oooooaOO1pzab.asDa",
                        "()*asddas-, ivan (&&&&&&&)(*******)..",
                        "helllo, i am a cat!")));
        Metadata actual = spellChecker.metadata(reader);
        Metadata expected = new Metadata(115, 10, 10);

        assertNotEquals("expected different # of words when multiple lines", expected, actual);
    }

    @Test
    public void testMetadataWrongMistakesCountMultipleLines() {
        Reader reader = new StringReader(String.join(System.lineSeparator(),
                List.of("asdasd&&&^ 1pesho^%$#@ 1pesho",
                        "&*gosho.. the,1PeShO,oooooaOO1pzab.asDa",
                        "()*asddas-, ivan (&&&&&&&)(*******)..",
                        "helllo, i am a cat!")));
        Metadata actual = spellChecker.metadata(reader);
        Metadata expected = new Metadata(115, 12, 9);

        assertNotEquals("expected different # of mistakes when multiple lines", expected, actual);
    }

    @Test
    public void testMetadataExpectedNothingFoundWhenEmptyText() {
        Reader reader = new StringReader(String.join(System.lineSeparator(), List.of("")));
        Metadata actual = spellChecker.metadata(reader);
        Metadata expected = new Metadata(0, 0, 0);

        assertEquals("expected nothing found metadata = (0,0,0)", expected, actual);
    }

    @Test(expected = IllegalArgumentException.class)
    public void findClosestWordsNullIllegalArgumentException() {
        spellChecker.findClosestWords(null, 10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void findClosestWordsNegativeIllegalArgumentException() {
        spellChecker.findClosestWords("word", -1);
    }

    @Test
    public void findClosestWordsNotNull() {
        assertNotNull("expected not null result", spellChecker.findClosestWords("helllo", 1));
    }

    @Test
    public void findClosestWordsCorrectResultTopOne() {
        List<String> actual = spellChecker.findClosestWords("helllo", 1);
        String expected = "hello";

        assertEquals("expecting exactly <hello> as suggestion", expected, actual.get(0));
    }

    @Test
    public void findClosestWordsWrongResultTopOne() {
        List<String> actual = spellChecker.findClosestWords("helllo", 1);
        String expected = "helloo";

        assertNotEquals("expecting that suggestion is not <helloo>", expected, actual.get(0));
    }

    @Test
    public void findClosestWordsCorrectResultTopTwo() {
        List<String> actual = spellChecker.findClosestWords("helllo", 2);
        List<String> expected = List.of("hello", "chello");

        assertTrue("expecting exactly the sam set",
                expected.containsAll(actual) && actual.containsAll(expected));
    }

    @Test
    public void findClosestWordsWrongResultTopTwo() {
        List<String> actual = spellChecker.findClosestWords("helllo", 2);
        List<String> expected = List.of("hello", "dog");

        assertFalse("expecting different set",
                expected.containsAll(actual) && actual.containsAll(expected));
    }

    @Test
    public void findClosestWordsCorrectResultTopThree() {
        List<String> actual = spellChecker.findClosestWords("helllo", 3);
        List<String> expected = List.of("hello", "chello", "jelly");

        assertTrue("expecting exactly the same set as result",
                expected.containsAll(actual) && actual.containsAll(expected));
    }

    @Test
    public void findClosestWordsWrongResultTopThree() {
        List<String> actual = spellChecker.findClosestWords("helllo", 3);
        List<String> expected = List.of("hello", "dog", "jelly");

        assertFalse("expecting different set as result",
                expected.containsAll(actual) && actual.containsAll(expected));
    }

    @Test(expected = IllegalArgumentException.class)
    public void analyzeExpectedIllegalArgumentExceptionNullReader() {
        Writer output = new StringWriter();
        spellChecker.analyze(null, output, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void analyzeExpectedIllegalArgumentExceptionNullWriter() {
        Reader reader = new StringReader("a");
        spellChecker.analyze(reader, null, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void analyzeExpectedIllegalArgumentExceptionNegativeNumber() {
        Reader reader = new StringReader("a");
        Writer output = new StringWriter();
        spellChecker.analyze(reader, output, -1);
    }

    @Test()
    public void analyzeExpectedRightOutput() {
        String[] expectedLines = new String[8];
        expectedLines[0] = "(%)*asddas-, ivan (&&&&&&&)(*******)..";
        expectedLines[1] = "helllo, i am a cat!";
        expectedLines[2] = "= = = Metadata = = =";
        expectedLines[3] = "51 characters, 4 words, 3 spelling issue(s) found";
        expectedLines[4] = "= = = Findings = = =";
        expectedLines[5] = "Line #1, {asddas} - Possible suggestions are {asdas, asda, fast}";
        expectedLines[6] = "Line #1, {ivan} - Possible suggestions are {hive, land, panic}";
        expectedLines[7] = "Line #2, {helllo} - Possible suggestions are {hello, chello, jelly}";

        Reader reader = new StringReader(String.join(System.lineSeparator(),
                List.of("(%)*asddas-, ivan (&&&&&&&)(*******)..",
                        "helllo, i am a cat!")));

        String expectedOutput = String.join(System.lineSeparator(), expectedLines);

        StringWriter writer = new StringWriter();

        spellChecker.analyze(reader, writer, 3);

        String[] actualLines = writer.toString().split(System.lineSeparator());

        String actualOutput = String.join(System.lineSeparator(), actualLines);
        assertEquals("expecting exactly the same output", expectedOutput, actualOutput);
    }

    @Test()
    public void analyzeExpectedWrongSuggestionOutput() {
        String[] expectedLines = new String[8];
        expectedLines[0] = "(%)*asddas-, ivan (&&&&&&&)(*******)..";
        expectedLines[1] = "helllo, i am a cat!";
        expectedLines[2] = "= = = Metadata = = =";
        expectedLines[3] = "51 characters, 4 words, 3 spelling issue(s) found";
        expectedLines[4] = "= = = Findings = = =";
        expectedLines[5] = "Line #1, {asddas} - Possible suggestions are {asdas, asda, fast}";
        expectedLines[6] = "Line #1, {ivan} - Possible suggestions are {hive, land, dog}";
        expectedLines[7] = "Line #2, {helllo} - Possible suggestions are {hello, chello, jelly}";

        Reader reader = new StringReader(String.join(System.lineSeparator(),
                List.of("(%)*asddas-, ivan (&&&&&&&)(*******)..",
                        "helllo, i am a cat!")));

        String expectedOutput = String.join(System.lineSeparator(), expectedLines);

        StringWriter writer = new StringWriter();

        spellChecker.analyze(reader, writer, 3);

        String[] actualLines = writer.toString().split(System.lineSeparator());

        String actualOutput = String.join(System.lineSeparator(), actualLines);
        assertNotEquals("expecting different output", expectedOutput, actualOutput);
    }
}
