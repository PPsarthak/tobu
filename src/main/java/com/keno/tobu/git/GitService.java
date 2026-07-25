package com.keno.tobu.git;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class GitService {

    public String getCurrentBranch() {
        CommandResult result = execute("git", "branch", "--show-current");
        if (result.isFailure()) {
            throw new RuntimeException("Failed to get current branch: " + result.error());
        }

        return result.output().trim();
    }

    public boolean hasUncommittedChanges() {
        CommandResult result = execute("git", "status", "--porcelain");
        if (result.isFailure()) {
            throw new RuntimeException("Failed to check git status: " + result.error());
        }

        return !result.output().isBlank();
    }

    public CommandResult stash(String stashName) {
        return execute("git", "stash", "push", "-u", "-m", "tobu: " + stashName);
    }

    public CommandResult pull(String branch) {
        return execute("git", "pull", "--no-edit", "origin", branch);
    }

    public CommandResult stashApply(String stashReference) {
        return execute("git", "stash", "apply", stashReference);
    }

    public boolean hasMergeConflicts() {
        CommandResult result = execute("git", "diff", "--name-only", "--diff-filter=U");
        if (result.isFailure()) {
            throw new RuntimeException("Failed to check for merge conflicts: " + result.error());
        }

        return !result.output().isBlank();
    }

    public String getLatestStash() {
        CommandResult result = execute("git", "stash", "list", "-1");
        if (result.isFailure()) {
            throw new RuntimeException("Failed to get latest stash: " + result.error());
        }

        return result.output().trim();
    }

    public String getLatestStashReference() {
        CommandResult result = execute("git", "stash", "list", "-1", "--format=%gd");
        if (result.isFailure()) {
            throw new RuntimeException("Failed to get latest stash reference: " + result.error());
        }

        return result.output().trim();
    }

    private CommandResult execute(String... command) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            Process process = processBuilder.start();

            List<String> output = new ArrayList<>();
            List<String> error = new ArrayList<>();

            try (BufferedReader outputReader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = outputReader.readLine()) != null) {
                    output.add(line);
                }
            }

            try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                String line;
                while ((line = errorReader.readLine()) != null) {
                    error.add(line);
                }
            }

            int exitCode = process.waitFor();
            return new CommandResult(
                    exitCode,
                    String.join(System.lineSeparator(), output),
                    String.join(System.lineSeparator(), error)
            );
        } catch (Exception e) {
            return new CommandResult(-1, "", e.getMessage());
        }
    }
}
