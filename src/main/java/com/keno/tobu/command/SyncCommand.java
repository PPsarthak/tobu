package com.keno.tobu.command;

import com.keno.tobu.service.SyncService;

public class SyncCommand {

    private final SyncService syncService;

    public SyncCommand(SyncService syncService) {
        this.syncService = syncService;
    }

    public void execute(String branch, String stashName) {
        syncService.execute(branch, stashName);
    }
}
