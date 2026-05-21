package com.example.callthematch.exception;

public class InviteCodeNotFound extends RuntimeException {
    public InviteCodeNotFound(String inviteCode) {
        super("Invitecode is not found ".formatted(inviteCode));
    }
}
