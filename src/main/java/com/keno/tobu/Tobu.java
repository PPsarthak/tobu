package com.keno.tobu;

import com.keno.tobu.git.GitService;

import java.util.logging.Logger;

public class Tobu {

    public static void main(String[] args) {

        GitService gitService = new GitService();

        String currentBranch = gitService.getCurrentBranch();
        System.out.println("Current branch: " + currentBranch);

        System.out.println("Has changes: " + gitService.hasUncommittedChanges());
    }

    private static void sync(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: tobu sync <branch> [stash-name]");
            return;
        }

        String branch = args[1];
        String stashName = args.length >= 3 ? args[2] : "tobu: auto-stash before sync";

        System.out.println("Sync command");
        System.out.println("Target branch: " + branch);
        System.out.println("Stash name: " + stashName);
    }

    private static void printHelp() {
        System.out.println("""
                Tobu - Personal Developer CLI
                
                Usage:
                  tobu sync <branch> [stash-name]

                Examples:
                  tobu sync dev
                  tobu sync dev "Changes"
                  tobu sync dev "Payment API work"
                """);
    }
}
