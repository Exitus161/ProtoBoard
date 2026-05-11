package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

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

        // Gibt eine nicht direkt veränderbare Sicht auf die Einträge zurück.
        // Änderungen sollen über addEntry(...) oder setEntries(...) passieren.
        return Collections.unmodifiableList(entries);
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

    /**
     * Ersetzt alle Einträge der Liste.
     */
    public void setEntries(List<String> entries) {
        this.entries = entries;
    }
}