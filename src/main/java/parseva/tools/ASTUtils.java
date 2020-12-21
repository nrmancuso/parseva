package parseva.tools;

import java.util.Map;

public final class ASTUtils {

    /**
     * Maps from a token name to value.
     */
    private static final Map<String, Integer> TOKEN_NAME_TO_VALUE;
    /**
     * Maps from a token value to name.
     */
    private static final String[] TOKEN_VALUE_TO_NAME;

    // initialise the constants
    static {
        TOKEN_NAME_TO_VALUE = TokenUtil.nameToValueMapFromPublicIntFields(JavaTokenTypes.class);
        TOKEN_VALUE_TO_NAME = TokenUtil.valueToNameArrayFromNameToValueMap(TOKEN_NAME_TO_VALUE);
    }

    /**
     * Returns the ID of a token for a given name.
     *
     * @param name the name of the token ID to get
     * @return a token ID
     */
    public static int getTokenId(String name) {
        final Integer id = TOKEN_NAME_TO_VALUE.get(name);
        if (id == null) {
            throw new IllegalArgumentException("Unknown token name. Given name " + name);
        }
        return id;
    }
}
