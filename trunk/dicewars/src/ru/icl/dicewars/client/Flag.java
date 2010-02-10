package ru.icl.dicewars.client;

public enum Flag {
	RED('R'), GREEN('G'), BLUE('B'), ORANGE('O'), YELLOW('Y'), CYAN('C'), MAGENTA('M'), BROWN('N'), GRAY('A'), CHARTREUSE('T'), WHITE('W');

    private char value;

    private Flag(char value) {
        this.value = value;
    }

    public char getCharValue() {
        return value;
    }
}
