package com.keno.tobu.validation;

import com.keno.tobu.exception.CommandValidationException;

public class CommandValidation {

    public void isSyncCommandValid(String[] args) {
        if (args.length < 2) {
            throw new CommandValidationException("Missing required argument: branch");
        }

        if (args.length > 3) {
            throw new CommandValidationException("Too many arguments for sync command");
        }

        String branch = args[1];
        if (branch.isBlank()) {
            throw new CommandValidationException("Branch name cannot be empty");
        }
        if (args.length == 3 && args[2].isBlank()) {
            throw new CommandValidationException(
                    "Stash name cannot be empty"
            );
        }
    }
}
