package bg.sofia.uni.fmi.mjt.spellchecker.validator;

public class Validator {
    private static final String KEYWORD_ARG_IS_NULL = " is null.";
    private static final String KEYWORD_NUMBER = "number(";
    private static final String KEYWORD_LESS_THAN_ZERO = ") is less than 0.";

    public static void validateNotNull(Object argument, String argumentName) {

        if (argument == null) {
            throw new IllegalArgumentException(argumentName + KEYWORD_ARG_IS_NULL);
        }
    }

    public static void validatePositive(int number) {
        if (number < 0) {
            throw new IllegalArgumentException(KEYWORD_NUMBER + number + KEYWORD_LESS_THAN_ZERO);
        }
    }
}
