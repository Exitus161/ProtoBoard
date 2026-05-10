package model;

/**
 * Abstrakte Oberklasse für alle Arten von Todo-Listen.
 * Enthält gemeinsame Eigenschaften (z. B. Titel).
 */
public abstract class TodoList {

    // Titel der Liste
    protected String title;

    /**
     * Konstruktor setzt den Titel der Liste
     */
    public TodoList(String title) {
        this.title = title;
    }

    /**
     * Gibt den Titel der Liste zurück
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gibt den Typ der Liste zurück.
     * Wird von den Unterklassen überschrieben.
     */
    public abstract String getType();
}