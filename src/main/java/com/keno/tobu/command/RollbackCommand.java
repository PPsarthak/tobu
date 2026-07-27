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

    public void execute(String stashName) {
        consoleLogger.info("Rolling back changes from stash: " + stashName);

        String stashReference = gitService.findByStashName(stashName);
        if (stashReference == null) {
            consoleLogger.error("Could not find stash: " + stashName);
            return;
        }

        consoleLogger.info("Found stash: " + stashReference);
        consoleLogger.info("Reverse applying that stash...");

        CommandResult rollbackResult = gitService.rollbackStash(stashReference);
        if (rollbackResult.isFailure()) {
            consoleLogger.error("Failed to rollback stash: " + rollbackResult.error());
            return;
        }

        consoleLogger.success("Stash rolled back successfully. The stash is also preserved");
    }

}
