package com.example.callthematch.exception;

public class InviteCodeNotFound extends RuntimeException {
    public InviteCodeNotFound(String inviteCode) {
        super("Invitecode %s is not found".formatted(inviteCode));
    }
}
