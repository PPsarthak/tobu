package com.keno.tobu.command;

import com.keno.tobu.console.ConsoleLogger;
import com.keno.tobu.git.CommandResult;
import com.keno.tobu.git.GitService;

public class StashRefreshCommand {

    private final GitService gitService;
    private final ConsoleLogger consoleLogger;

    public StashRefreshCommand(GitService gitService, ConsoleLogger consoleLogger) {
        this.gitService = gitService;
        this.consoleLogger = consoleLogger;
    }

    public void execute(String stashName) {
        consoleLogger.info("Refreshing stash: " + stashName);

        String oldStashReference = gitService.findByStashName(stashName);
        if (oldStashReference == null) {
            consoleLogger.error("Could not find stash: " + stashName);
            return;
        }

        consoleLogger.info("Found stash: " + oldStashReference);

        String oldStashCommitHash = gitService.getStashCommitHash(oldStashReference);
        if (oldStashCommitHash == null) {
            consoleLogger.error("Could not determine stash commit hash.");
            return;
        }
        consoleLogger.info("Applying existing stash...");

        CommandResult stashApplyResult = gitService.stashApply(oldStashReference);
        if (stashApplyResult.isFailure()) {
            consoleLogger.error("Failed to apply stash: " + stashApplyResult.error());
            consoleLogger.warning("The original stash has not been removed");
            return;
        }

        consoleLogger.info("Stash applied successfully. Creating a fresh stash...");

        CommandResult newStashResult = gitService.stash(stashName);
        if (newStashResult.isFailure()) {
            consoleLogger.error("Failed to create a fresh stash: " + newStashResult.error());
            consoleLogger.warning("The original stash has not been removed");
            return;
        }

        String newStashReference = gitService.getLatestStashReference();
        consoleLogger.info("Fresh stash created: " + newStashReference);
        consoleLogger.info("Removing old stash: " + oldStashReference);

        String oldStashAfterRefresh = gitService.findStashByCommitHash(oldStashCommitHash);
        if (oldStashAfterRefresh == null) {
            consoleLogger.error("Fresh stash was created, but the original stash could not be found.");
            consoleLogger.warning("Both the old and new stash are currently preserved.");
            return;
        }

        consoleLogger.info("Removing old stash: " + oldStashAfterRefresh);

        CommandResult dropStashResult = gitService.dropStash(oldStashReference);
        if (dropStashResult.isFailure()) {
            consoleLogger.error("Failed to drop the old stash: " + dropStashResult.error());
            consoleLogger.warning("New stash: " + newStashReference + " Old stash: " + oldStashReference);
            return;
        }

        consoleLogger.success("Stash refreshed successfully!");
    }
}
