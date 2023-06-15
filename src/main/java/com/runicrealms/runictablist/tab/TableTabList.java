package com.runicrealms.runictablist.tab;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A tab list implementation that supports column and row support
 */
public class TableTabList extends TabList {
    public TableTabList(@NotNull Player player, @Nullable String header, @Nullable String footer) {
        super(player, header, footer);
    }

    /**
     * A method used to set an element in a table tab list
     *
     * @param element the element to be set
     * @param column  the column index
     * @param row     the row index
     */
    public void set(@NotNull TabElement element, int column, int row) {
        //no need for conditions, the next call to set already takes care of that
        this.set(element, this.getIndex(column, row));
    }

    public void remove(int column, int row) {
        //no need for conditions, the HashMap implementation of Map in the superclass takes care of that
        this.remove(this.getIndex(column, row));
    }

    /**
     * A method that does some quick math to convert rows and columns to an index
     *
     * @param column the column
     * @param row    the row
     * @return the index of the intended element
     */
    private int getIndex(int column, int row) {
        return (column * 20) + row; //there are 20 rows per column
    }
}
