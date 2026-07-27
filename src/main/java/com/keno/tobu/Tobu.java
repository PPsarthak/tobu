package com.keno.tobu;

import com.keno.tobu.command.*;
import com.keno.tobu.console.ConsoleLogger;
import com.keno.tobu.exception.CommandValidationException;
import com.keno.tobu.git.GitService;
import com.keno.tobu.service.SyncService;
import com.keno.tobu.validation.CommandValidation;

import static com.keno.tobu.constant.Constant.*;

public class Tobu {

    private static final ConsoleLogger consoleLogger = new ConsoleLogger();

    public static void main(String[] args) {
        if (args.length == 0) {
            info();
            return;
        }

        String command = args[0];
        try {
            switch (command) {
                case SYNC -> sync(args);
                case VERSION -> version();
                case INFO -> info();
                case ROLLBACK ->  rollback(args);
                case "stash-refresh" -> stashRefresh(args);
                default -> {
                    consoleLogger.error("Unknown command: " + command);
                    info();
                }
            }
        } catch (CommandValidationException e) {
            info();
            throw new RuntimeException(e);
        }
    }

    private static void sync(String[] args) {
        CommandValidation validator = new CommandValidation();
        validator.isSyncCommandValid(args);

        String branch = args[1];
        String stashName = args.length >= 3 ? args[2] : "auto-stash before sync";

        GitService gitService = new GitService();
        SyncService syncService = new SyncService(gitService, consoleLogger);
        SyncCommand syncCommand = new SyncCommand(syncService);

        syncCommand.execute(branch, stashName);
    }

    private static void stashRefresh(String[] args) {
        GitService gitService = new GitService();
        StashRefreshCommand stashRefreshCommand = new StashRefreshCommand(gitService, consoleLogger);
        stashRefreshCommand.execute(args[1]);
    }

    private static void rollback(String[] args) {
        GitService gitService = new GitService();
        RollbackCommand rollbackCommand = new RollbackCommand(gitService, consoleLogger);
        rollbackCommand.execute(args);
    }

    private static void version() {
        VersionCommand versionCommand = new VersionCommand(consoleLogger);
        versionCommand.execute();
    }

    private static void info() {
        InfoCommand infoCommand = new InfoCommand(consoleLogger);
        infoCommand.execute();
    }
}
