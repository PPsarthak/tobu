package com.keno.tobu;

import com.keno.tobu.command.SyncCommand;
import com.keno.tobu.git.GitService;

public class Tobu {

    public static void main(String[] args) {
        if (args.length == 0) {
            printHelp();
            return;
        }

        String command = args[0];
        if (command.equals("sync")) {
            sync(args);
        } else {
            System.out.println("Unknown command: " + command);
            printHelp();
        }
    }

    private static void sync(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: tobu sync <branch> [stash-name]");
            return;
        }

        String branch = args[1];
        String stashName = args.length >= 3 ? args[2] : "auto-stash before sync";

        GitService gitService = new GitService();
        SyncCommand syncCommand = new SyncCommand(gitService);

        syncCommand.execute(branch, stashName);
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
