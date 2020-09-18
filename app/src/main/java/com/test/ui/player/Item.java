package com.test.ui.player;

/**
 * Класс описывающий аудио
 */

public class Item {

    private String fileName;

    public Item(String name) {
        this.fileName = name;
    }

    public String getName() {
        return fileName;
    }

    public void setName(String name) {
        this.fileName = name;
    }

    @Override
    public String toString() {
        return "Item{" +
                "fileName='" + fileName + '\'' +
                '}';
    }
}
