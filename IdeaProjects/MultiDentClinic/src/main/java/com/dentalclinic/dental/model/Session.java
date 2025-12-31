package com.dentalclinic.dental.model;

public final class Session {
    private Session() {}

    private static User currentUser;

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static void clear() {
        currentUser = null;
    }

    public static boolean isAuthenticated() {
        return currentUser != null;

    }

    public static Long getCurrentUserId() {
        return currentUser == null ? null : currentUser.getId();
    }

    public static String getCurrentUsername() {
        return currentUser == null ? null : currentUser.getUsername();
    }
}
