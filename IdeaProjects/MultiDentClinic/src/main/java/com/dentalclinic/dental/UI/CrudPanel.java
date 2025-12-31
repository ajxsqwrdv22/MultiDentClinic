package com.dentalclinic.dental.UI;

/**
 * Panels that support toolbar operations should implement this.
 */
public interface CrudPanel {
    /**
     * Refresh list / table.
     */
    void refresh();

    /**
     * Quick-add a default/example record (used by toolbar).
     */

    /**
     * Apply a search/filter. If query is null or empty, show all.
     * @param query search string (case-insensitive)
     */
    void search(String query);
}
