package com.dentalclinic.dental.UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Top polished toolbar: search, refresh, quick-add.
 */
public class TopToolbar extends JToolBar {
    private final JTextField searchField = new JTextField(24);
    private final JButton btnSearch = new JButton("Search");
    private final JButton btnClear = new JButton("Clear");
    private final JButton btnRefresh = new JButton("Refresh");

    // Toolbar emits actions that MainFrame listens to via ActionListener
    public TopToolbar(ActionListener listener) {
        setFloatable(false);
        setLayout(new FlowLayout(FlowLayout.LEFT, 8, 6));

        // Search group
        add(new JLabel("Search:"));
        add(searchField);
        btnSearch.setActionCommand("toolbar_search");
        btnSearch.addActionListener(e -> listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "toolbar_search")));
        add(btnSearch);

        btnClear.setActionCommand("toolbar_clear");
        btnClear.addActionListener(e -> {
            searchField.setText("");
            listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "toolbar_clear"));
        });
        add(btnClear);

        // Refresh
        btnRefresh.setActionCommand("toolbar_refresh");
        btnRefresh.addActionListener(listener);
        add(btnRefresh);

        addSeparator(new Dimension(12, 0));

    }

    public String getSearchText() {
        return searchField.getText();
    }
}
