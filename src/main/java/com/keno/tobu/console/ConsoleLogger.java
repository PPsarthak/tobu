package com.keno.tobu.console;

public class ConsoleLogger {

    private static final String RESET = "\u001B[0m";

    private static final String BLUE = "\u001B[34m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED = "\u001B[31m";

    public void info(String message) {
        print(ConsoleColor.BLUE, "INFO", message);
    }

    public void success(String message) {
        print(ConsoleColor.GREEN, "SUCCESS", message);
    }

    public void warning(String message) {
        print(ConsoleColor.YELLOW, "WARNING", message);
    }

    public void error(String message) {
        print(ConsoleColor.RED, "ERROR", message);
    }

    private void print(ConsoleColor color, String level, String message) {
        System.out.println(color.getCode() + "[" + level + "] " + message + ConsoleColor.RESET.getCode());
    }
}