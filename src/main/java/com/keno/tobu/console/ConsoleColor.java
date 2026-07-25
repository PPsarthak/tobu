package com.keno.tobu.console;

public enum ConsoleColor {

    BLUE("\u001B[34m"),
    GREEN("\u001B[32m"),
    YELLOW("\u001B[33m"),
    RED("\u001B[31m"),
    RESET("\u001B[0m");

    private final String code;

    ConsoleColor(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
