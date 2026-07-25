package com.keno.tobu.command;

import com.keno.tobu.console.ConsoleLogger;

public class InfoCommand {
    private final ConsoleLogger consoleLogger;

    public InfoCommand(ConsoleLogger consoleLogger) {
        this.consoleLogger = consoleLogger;
    }

    public void execute() {
        consoleLogger.info("""
                Tobu - Personal Developer CLI

                Usage:
                  tobu <command> [arguments]

                Commands:
                  sync      Sync current branch with another branch
                  version   Display Tobu version

                Examples:
                  tobu sync dev
                  tobu sync dev "Payment API work"

                  tobu version
                """);
    }
}
