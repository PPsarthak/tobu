package com.keno.tobu.git;

import com.keno.tobu.console.ConsoleLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.keno.tobu.constant.Constant.GIT;

public class GitService {

    public String getCurrentBranch() {
        CommandResult result = execute(GIT, "branch", "--show-current");
        if (result.isFailure()) {
            throw new RuntimeException("Failed to get current branch: " + result.error());
        }

        return result.output().trim();
    }

    public boolean hasUncommittedChanges() {
        CommandResult result = execute(GIT, "status", "--porcelain");
        if (result.isFailure()) {
            throw new RuntimeException("Failed to check git status: " + result.error());
        }

        return !result.output().isBlank();
    }

    public CommandResult stash(String stashName) {
        return execute(GIT, "stash", "push", "-u", "-m", "tobu: " + stashName);
    }

    public CommandResult pull(String branch) {
        return execute(GIT, "pull", "--no-edit", "origin", branch);
    }

    public CommandResult stashApply(String stashReference) {
        return execute(GIT, "stash", "apply", stashReference);
    }

    public boolean hasMergeConflicts() {
        CommandResult result = execute(GIT, "diff", "--name-only", "--diff-filter=U");
        if (result.isFailure()) {
            throw new RuntimeException("Failed to check for merge conflicts: " + result.error());
        }

        return !result.output().isBlank();
    }

    public String getLatestStashReference() {
        CommandResult result = execute(GIT, "stash", "list", "-1", "--format=%gd");
        if (result.isFailure()) {
            throw new RuntimeException("Failed to get latest stash reference: " + result.error());
        }

        return result.output().trim();
    }

    public String findByStashName(String stashName) {
        CommandResult result = execute(GIT,"stash","list","--format=%gd|%gs");
        if (result.isFailure()) {
            return null;
        }

        return Arrays.stream(result.output().split("\\R"))
                .map(String::trim)
                .map(line -> line.replace("'", ""))   // optional safety
                .filter(line -> {
                    String[] parts = line.split("\\|", 2);
                    return parts.length == 2
                            && parts[1].endsWith("tobu: " + stashName);
                })
                .map(line -> line.substring(0, line.indexOf('|')))
                .findFirst()
                .orElse(null);
    }

    public String findStashByCommitHash(String commitHash) {
        CommandResult result = execute(GIT,"stash","list","--format=%gd %H");

        if (result.isFailure()) {
            return null;
        }

        return Arrays.stream(result.output().split("\\R"))
                .filter(line -> line.endsWith(commitHash))
                .map(line -> line.substring(0, line.indexOf(" ")))
                .findFirst()
                .orElse(null);
    }

    public CommandResult dropStash(String stashReference) {
        return execute(GIT, "stash", "drop", stashReference);
    }

    public String getStashCommitHash(String stashReference) {
        CommandResult result = execute(GIT,"rev-parse",stashReference);
        if (result.isFailure()) {
            return null;
        }

        return result.output().trim();
    }

    public CommandResult getStashPatch(String stashReference) {
        return execute(
                GIT,
                "stash",
                "show",
                "--binary",
                "--format=",
                stashReference
        );
    }

    public CommandResult rollbackStash(String stashReference) {
        CommandResult patchResult = getStashPatch(stashReference);

        if (patchResult.isFailure()) {
            return patchResult;
        }

        return applyReversePatch(patchResult.output());
    }

    private CommandResult applyReversePatch(String patch) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(GIT,"apply","-R","--whitespace=nowarn");
            Process process = processBuilder.start();
            process.getOutputStream().write(patch.getBytes(StandardCharsets.UTF_8));
            process.getOutputStream().close();

            String output = readStream(process.getInputStream());
            String error = readStream(process.getErrorStream());

            int exitCode = process.waitFor();

            return new CommandResult(exitCode, output, error);
        } catch (Exception e) {
            return new CommandResult(-1,"",e.getMessage());
        }
    }

    private String readStream(InputStream inputStream) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            return reader.lines().collect(Collectors.joining(System.lineSeparator()));
        }
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
