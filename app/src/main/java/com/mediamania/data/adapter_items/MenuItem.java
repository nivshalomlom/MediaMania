package com.mediamania.data.adapter_items;

public class MenuItem {

    private String text;
    private int icon_id;

    /**
     * Creates a new menu item
     * @param text The title of the item
     * @param icon_id The resource id of the icon
     */
    public MenuItem(String text, int icon_id) {
        this.text = text;
        this.icon_id = icon_id;
    }

    public String getText() { return text; }

    public int getIconId() { return icon_id; }

    public void setText(String text) { this.text = text; }

    public void setIcon_id(int icon_id) { this.icon_id = icon_id; }
}
