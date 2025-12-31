package com.dentalclinic.dental.security;

import com.dentalclinic.dental.model.User;

import javax.swing.*;
import java.awt.*;

public final class AccessControl {

    // ===============================
    // PERMISSION CONSTANTS
    // ===============================
    public static final String MANAGE_ALL = "MANAGE_ALL";

    public static final String MANAGE_APPOINTMENTS = "MANAGE_APPOINTMENTS";
    public static final String CREATE_APPOINTMENT = "CREATE_APPOINTMENT";

    public static final String MANAGE_PATIENTS = "MANAGE_PATIENTS";
    public static final String MANAGE_DENTISTS = "MANAGE_DENTISTS";
    public static final String MANAGE_CLINICS = "MANAGE_CLINICS";
    public static final String MANAGE_SERVICES = "MANAGE_SERVICES";
    public static final String MANAGE_USERS = "MANAGE_USERS";

    private AccessControl() {}

    // ===============================
    // CORE PERMISSION CHECK
    // ===============================
    public static boolean hasPermission(User user, String permission) {
        if (user == null ) {
            return false;
        }

        // ADMIN can do everything
        if (user.hasRole("ADMIN")) {
            return true;
        }

        // STAFF permissions
        if (user.hasRole("STAFF")) {
            return switch (permission) {
                case CREATE_APPOINTMENT,
                     MANAGE_APPOINTMENTS,
                     MANAGE_PATIENTS -> true;

                default -> false;
            };
        }

        // Default: no access
        return false;
    }

    // ===============================
    // UI GUARD (SHOWS MESSAGE)
    // ===============================
    public static boolean requirePermission(
            Component parent,
            String permission,
            String actionDescription
    ) {
        if (!hasPermission(
                com.dentalclinic.dental.model.Session.getCurrentUser(),
                permission
        )) {
            JOptionPane.showMessageDialog(
                    parent,
                    "You do not have permission to " + actionDescription + ".",
                    "Access denied",
                    JOptionPane.WARNING_MESSAGE
            );
            return false;
        }
        return true;
    }
}
