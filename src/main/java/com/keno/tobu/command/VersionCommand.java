package com.keno.tobu.command;

import com.keno.tobu.console.ConsoleLogger;

import static com.keno.tobu.constant.Constant.TOBU_VERSION;

public class VersionCommand {

    private final ConsoleLogger consoleLogger;

    public VersionCommand(ConsoleLogger consoleLogger) {
        this.consoleLogger = consoleLogger;
    }

    public void execute() {
        consoleLogger.info("Tobu CLI v" + TOBU_VERSION);
    }
}