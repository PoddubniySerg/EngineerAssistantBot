package ru.land.service.parser.brigade;

public class BrigadeParser {

    protected final static int SIZE_BRIGADE_ID = 4;

    private int brigade;

    public int parse() {
        return brigade;
    }

    public boolean canBeParsed(String string) {
        if (string.length() >= SIZE_BRIGADE_ID) {
            final var words = string.split(" ");
            final var len = string.length();
            if (words[words.length - 1].equals("МСК") || words[1].equals("РЗН")) {
                try {
                    final var length = words[0].length();
                    final var brigadeId = length > 4 ? words[0].substring(1) : words[0];
                    brigade = Integer.parseInt(brigadeId);
                    return true;
                } catch (NumberFormatException e) {
                    return false;
                }
            }
        }
        return false;
    }
}
