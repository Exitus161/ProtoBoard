package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Todo-Liste für einfache Freitext-Einträge.
 * Diese Liste besitzt keine Checkboxen.
 */
public class TextTodoList extends TodoList {

    // Liste aller Textelemente
    private List<String> entries;

    /**
     * Konstruktor erstellt eine neue Freitext-Liste.
     */
    public TextTodoList(String title) {
        super(title, "text");
        entries = new ArrayList<>();
    }

    /**
     * Leerer Konstruktor für Gson
     */
    public TextTodoList() {
        super("", "text");
        entries = new ArrayList<>();
    }

    /**
     * Gibt alle Einträge zurück.
     */
    public List<String> getEntries() {
        return entries;
    }

    /**
     * Fügt einen neuen Eintrag hinzu.
     */
    public void addEntry(String entry) {
        entries.add(entry);
    }

    /**
     * Entfernt einen Eintrag.
     */
    public void removeEntry(String entry) {
        entries.remove(entry);
    }
}