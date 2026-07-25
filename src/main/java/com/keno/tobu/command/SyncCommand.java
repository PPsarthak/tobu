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

        boolean stashCreated = false;
        String stashReference = null;
        boolean hasChanges = gitService.hasUncommittedChanges();
        if (hasChanges) {
            System.out.println("Branch " + currentBranch + " has uncommitted changes");
            System.out.println("Creating stash: " + stashName);

            CommandResult stashResult = gitService.stash(stashName);
            if (stashResult.isFailure()) {
                System.out.println("Failed to create stash: " + stashResult.error());
                return;
            }
            stashCreated = true;
            stashReference = gitService.getLatestStashReference();
            System.out.println("Changes stashed successfully: " + stashReference);
        } else {
            System.out.println("No uncommitted changes");
        }

        CommandResult pullResult = gitService.pull(branch);
        if (pullResult.isFailure()) {
            System.out.println("Failed to pull latest changes: " + pullResult.error());
            if (stashCreated) {
                System.out.println("Your changes are still safely stored in the stash");
            }
            return;
        }

        System.out.println("Successfully pulled latest changes from branch: " + branch + " " + pullResult.output());
        if (gitService.hasMergeConflicts()) {
            System.out.println("Pull resulted in merge conflicts");
            if (stashCreated) {
                System.out.println("Your local changes are still safely stashed: " + stashReference);
                System.out.println("Resolve the merge conflicts before applying your stashed changes");
                return;
            }
        }

        if (stashCreated) {
            System.out.println("Applying previously stashed changes...");

            CommandResult stashApplyResult = gitService.stashApply(stashReference);
            if (stashApplyResult.isFailure()) {
                System.out.println("Failed to apply stash: " + stashApplyResult.error());

                if (gitService.hasMergeConflicts()) {
                    System.out.println("Your stashed changes resulted in a merge conflict, but your changes are stashed: " + stashReference);
                    System.out.println("Run git status to see conflicted files");
                }
                return;
            }
            System.out.println("Stash applied successfully!");
        }
    }
}
