package org.myshelfie.view.CLI;

/**
 * Colors used in the CLI
 */
public enum Color {
    BLACK("\033[30m"),
    RED("\033[31m"),
    GREEN("\033[32m"),
    YELLOW("\033[33m"),
    BLUE("\033[34m"),
    LIGHT_GRAY("\033[37m"),
    UWhite("\033[4;37m"),
    ULight_gray("\033[4;38;2;105;105;105m"),
    MAGENTA("\033[35m"),
    CYAN("\033[36m"),
    BG_RED("\033[41m"),
    BG_GREEN("\033[42m"),
    BG_YELLOW("\033[43m"),
    BG_BLUE("\033[44m"),
    BG_MAGENTA("\033[45m"),
    BG_CYAN("\033[46m"),
    BG_BRIGHT_RED("\033[101m"),
    BG_BRIGHT_GREEN("\033[102m"),
    BG_BRIGHT_YELLOW("\033[103m"),
    BG_LIGHT_BLUE("\033[104m"),
    BG_LIGHT_BROWN("\033[48;2;128;84;09m"),
    BG_DARK_BROWN("\033[48;2;74;49;06m"),
    BG_TITLE_FRAME("\033[48;2;158;92;11m"),
    BG_TITLE_FILL("\033[48;2;74;43;05m"),
    BG_LIGHT_CYAN("\033[48;2;99;148;142m"),
    BG_DARK_GRAY("\033[48;2;71;79;78m"),
    BG_GRAY1("\033[48;2;140;140;140m"),
    BG_BRIGHT_BLUE("\033[48;2;40;69;186m"),
    BG_DARK_RED("\033[48;2;49;03;07m"),

    RESET("\033[0m");

    private final String s;

    Color(String s) {
        this.s = s;
    }

    @Override
    public String toString() {
        return s;
    }
}
