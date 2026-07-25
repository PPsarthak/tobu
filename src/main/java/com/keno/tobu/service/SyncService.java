package com.keno.tobu.service;

import com.keno.tobu.console.ConsoleLogger;
import com.keno.tobu.git.CommandResult;
import com.keno.tobu.git.GitService;

public class SyncService {

    private final GitService gitService;
    private final ConsoleLogger consoleLogger;

    public SyncService(GitService gitService, ConsoleLogger consoleLogger) {
        this.gitService = gitService;
        this.consoleLogger = consoleLogger;
    }

    public void execute(String branch, String stashName) {
        consoleLogger.info("Syncing current branch with origin/" + branch);

        String currentBranch = gitService.getCurrentBranch();
        consoleLogger.info("Current branch: " + currentBranch);

        boolean stashCreated = false;
        String stashReference = null;
        boolean hasChanges = gitService.hasUncommittedChanges();
        if (hasChanges) {
            consoleLogger.info("Branch " + currentBranch + " has uncommitted changes");
            consoleLogger.info("Creating stash: " + stashName);

            CommandResult stashResult = gitService.stash(stashName);
            if (stashResult.isFailure()) {
                consoleLogger.error("Failed to create stash: " + stashResult.error());
                return;
            }
            stashCreated = true;
            stashReference = gitService.getLatestStashReference();
            consoleLogger.info("Changes stashed successfully: " + stashReference);
        } else {
            consoleLogger.info("No uncommitted changes");
        }

        CommandResult pullResult = gitService.pull(branch);
        if (pullResult.isFailure()) {
            consoleLogger.error("Failed to pull latest changes: " + pullResult.error());
            if (stashCreated) {
                consoleLogger.warning("Your changes are still safely stored in the stash");
            }
            return;
        }

        if (gitService.hasMergeConflicts()) {
            consoleLogger.error("Pull resulted in merge conflicts");
            if (stashCreated) {
                consoleLogger.warning("Your local changes are still safely stashed: " + stashReference);
                consoleLogger.warning("Resolve the merge conflicts before applying your stashed changes");
                return;
            }
        }

        consoleLogger.info("Successfully pulled latest changes from branch: origin/" + branch + " " + pullResult.output());

        if (stashCreated) {
            consoleLogger.info("Applying previously stashed changes...");

            CommandResult stashApplyResult = gitService.stashApply(stashReference);
            if (stashApplyResult.isFailure()) {
                consoleLogger.error("Failed to apply stash: " + stashApplyResult.error());

                if (gitService.hasMergeConflicts()) {
                    consoleLogger.warning("Your stashed changes resulted in a merge conflict, but your changes are stashed: " + stashReference);
                    consoleLogger.warning("Run git status to see conflicted files");
                }
                return;
            }
            consoleLogger.info("Stash applied successfully!");

            consoleLogger.success("Sync Complete");
        }
    }
}
