package com.keno.tobu.command;

import com.keno.tobu.git.CommandResult;
import com.keno.tobu.git.GitService;

public class SyncCommand {

    private final GitService gitService;

    public SyncCommand(GitService gitService) {
        this.gitService = gitService;
    }

    public void execute(String branch, String stashName) {
        System.out.println("Syncing current branch with origin/" + branch);

        String currentBranch = gitService.getCurrentBranch();
        System.out.println("Current branch: " + currentBranch);

        boolean hasChanges = gitService.hasUncommittedChanges();
        if (hasChanges) {
            System.out.println("Branch " + currentBranch + " has uncommitted changes");
            System.out.println("Creating stash: " + stashName);

            CommandResult stashResult = gitService.stash(stashName);
            if (stashResult.isFailure()) {
                System.out.println("Failed to create stash: " + stashResult.error());
                return;
            }
            System.out.println("Changes stashed successfully!");
        }
        else {
            System.out.println("No uncommitted changes");
        }

        CommandResult pullResult = gitService.pull(branch);
        if (pullResult.isFailure()) {
            System.out.println("Failed to pull latest changes: " + pullResult.error());
            return;
        }

        System.out.println("Successfully pulled latest changes from branch: " + branch);

        CommandResult stashApplyResult = gitService.stashApply();
        if (stashApplyResult.isFailure()) {
            System.out.println("Failed to apply stash: " + stashApplyResult.error());
            return;
        }
        System.out.println("Stash applied successfully");
    }
}
