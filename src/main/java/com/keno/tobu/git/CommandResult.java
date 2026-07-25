package com.keno.tobu.git;

public record CommandResult(int exitCode, String output, String error) {

    public boolean isSuccess() {
        return exitCode == 0;
    }

    public boolean isFailure() {
        return exitCode != 0;
    }

    @Override
    public String toString() {
        return "CommandResult{" +
                "exitCode=" + exitCode +
                ", output='" + output + '\'' +
                ", error='" + error + '\'' +
                '}';
    }
}
