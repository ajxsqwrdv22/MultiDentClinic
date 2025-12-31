package com.dentalclinic.dental.UI;

import com.dentalclinic.dental.model.Session;

import javax.swing.*;
import java.awt.*;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
 * Logs out the user after a period of inactivity.
 * Usage:
 *   SessionTimeoutManager stm = new SessionTimeoutManager(frame, timeoutSeconds);
 *   stm.start();
 *   stm.stop(); // when shutting down
 *
 * It listens to global AWT events (mouse/keyboard) and resets the timer when activity is detected.
 */
public class SessionTimeoutManager {
    private final Window owner;
    private final int timeoutSeconds;
    private final Timer timer;
    private final AWTEventListener awtListener;

    public SessionTimeoutManager(Window owner, int timeoutSeconds) {
        this.owner = owner;
        this.timeoutSeconds = timeoutSeconds;
        this.timer = new Timer(timeoutSeconds * 1000, e -> onTimeout());
        this.timer.setRepeats(false);

        this.awtListener = evt -> {
            if (evt instanceof MouseEvent || evt instanceof KeyEvent) {
                resetTimer();
            }
        };
    }

    public void start() {
        Toolkit.getDefaultToolkit().addAWTEventListener(awtListener,
                AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK | AWTEvent.KEY_EVENT_MASK);
        resetTimer();
    }

    public void resetTimer() {
        if (timer.isRunning()) timer.restart(); else timer.start();
    }

    public void stop() {
        timer.stop();
        try {
            Toolkit.getDefaultToolkit().removeAWTEventListener(awtListener);
        } catch (Exception ignored) {}
    }

    private void onTimeout() {
        // Simple logout flow: clear session, dispose owner frames, show login
        Session.clear();
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(owner, "Session timed out due to inactivity. You will be returned to the login screen.", "Session timeout", JOptionPane.INFORMATION_MESSAGE);
            // close all frames
            for (Frame f : Frame.getFrames()) {
                if (f.isDisplayable()) f.dispose();
            }
            LoginFrame.showLogin();
        });
    }
}
