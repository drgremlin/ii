package org.ayfaar.app.services.moderation;

public enum AccessLevel {
    ROLE_ADMIN(0), ROLE_EDITOR(1);

    private int precedence;

    private AccessLevel(int precedence) {
        this.precedence = precedence;
    }


    public static AccessLevel fromPrecedence(int precedence) {
        for (AccessLevel accessLevel : AccessLevel.values()) {
            if (accessLevel.getPrecedence() == precedence) {
                return accessLevel;
            }
        }
        return null;
    }


    public int getPrecedence() {
        return precedence;
    }
}
