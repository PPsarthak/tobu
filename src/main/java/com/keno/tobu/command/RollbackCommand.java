package com.keno.tobu.command;

import com.keno.tobu.console.ConsoleLogger;
import com.keno.tobu.git.CommandResult;
import com.keno.tobu.git.GitService;

public class RollbackCommand {

    private final GitService gitService;
    private final ConsoleLogger consoleLogger;

    public RollbackCommand(GitService gitService, ConsoleLogger consoleLogger) {
        this.gitService = gitService;
        this.consoleLogger = consoleLogger;
    }

    public void execute(String[] arg) {
        String stashName = arg[1];
        consoleLogger.info("Rolling back changes from stash: " + stashName);
        String stashReference = gitService.findByStashName(stashName);
        if (stashReference == null) {
            consoleLogger.error("Could not find stash: " + stashName);
            return;
        }

        consoleLogger.info("Found stash: " + stashReference);

    }

}
