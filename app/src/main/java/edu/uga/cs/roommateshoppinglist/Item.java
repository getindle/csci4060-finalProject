package edu.uga.cs.roommateshoppinglist;

import java.io.Serializable;

public class Item implements Serializable {

    private String itemName;
    private String itemQuantity;
    private String itemKey;

    public Item() {

    }

    public Item(String itemName, String itemQuantity) {
        this(itemName, itemQuantity, null);
    }


    public Item(String itemName, String itemQuantity, String itemKey) {
        this.itemName = itemName;
        this.itemQuantity = itemQuantity;
        this.itemKey = itemKey;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setItemQuantity(String itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    public void setItemKey(String itemKey) {
        this.itemKey = itemKey;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemQuantity() {
        return itemQuantity;
    }

    public String getItemKey() { return itemKey; }

    public void clearKey() {
        itemKey = null;
    }

    public boolean equals(String itemKey) {

        return this.itemKey.equals(itemKey);
    }


}
