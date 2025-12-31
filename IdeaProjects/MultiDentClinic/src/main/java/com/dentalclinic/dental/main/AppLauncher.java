package com.dentalclinic.dental.main;

import com.dentalclinic.dental.UI.LoginFrame;
import com.dentalclinic.dental.util.DbConnectionPool;

import javax.swing.*;

public class AppLauncher {

    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        // Test DB connection before launching
        if (!DbConnectionPool.testConnection()) {
            JOptionPane.showMessageDialog(
                    null,
                    "Unable to connect to the database. \n" +
                            "Please check your application.properties\n" +
                            "or MySQL server.",

                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // Launch Login Screen
        SwingUtilities.invokeLater(() -> {
            LoginFrame login = new LoginFrame();
            login.setVisible(true);
        });
    }
}
