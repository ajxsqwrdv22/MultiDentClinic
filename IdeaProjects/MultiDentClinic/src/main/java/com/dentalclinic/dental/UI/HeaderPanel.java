package com.dentalclinic.dental.UI;

import javax.swing.*;
import java.awt.*;

/**
 * Simple header panel with title (left) and status (right).
 * MainFrame will place toolbar and right-side controls next to this.
 */
public class HeaderPanel extends JPanel {
    private final JLabel titleLabel = new JLabel();
    private final JLabel statusLabel = new JLabel();

    public HeaderPanel() {
        setLayout(new BorderLayout(8,8));
        setBorder(BorderFactory.createEmptyBorder(8,12,8,12));
        init();
    }

    private void init() {
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 18f));
        titleLabel.setText("<html><b>Dental Clinic</b></html>");
        titleLabel.setHorizontalAlignment(SwingConstants.LEFT);

        statusLabel.setFont(statusLabel.getFont().deriveFont(Font.PLAIN, 12f));
        statusLabel.setText("Not signed in");
        statusLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        add(titleLabel, BorderLayout.WEST);
        add(statusLabel, BorderLayout.EAST);
    }

    /**
     * Set the main title shown in the header.
     * Eg. header.setTitle("Patients");
     */
    public void setTitle(String t) {
        if (t == null || t.isBlank()) {
            titleLabel.setText("<html><b>Dental Clinic</b></html>");
        } else {
            titleLabel.setText("<html><b>" + escapeHtml(t) + "</b></html>");
        }
    }

    /**
     * Set the small status text on the right, e.g. "admin | connected".
     */
    public void setStatus(String s) {
        if (s == null) s = "";
        statusLabel.setText(escapeHtml(s));
    }

    /**
     * Convenience: set user status (username)
     */
    public void setUser(String username) {
        if (username == null || username.isBlank()) {
            setStatus("Not signed in");
        } else {
            setStatus("Signed in: " + username);
        }
    }

    // minimal escaping for HTML labels
    private String escapeHtml(String in) {
        return in.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
