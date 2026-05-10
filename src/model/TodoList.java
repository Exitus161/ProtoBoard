package model;

/**
 * Abstrakte Oberklasse für alle Arten von Todo-Listen.
 * Enthält gemeinsame Eigenschaften (z. B. Titel).
 */
public abstract class TodoList {

    // Titel der Liste
    protected String title;
    // Typ der Liste
    protected String type;

    /**
     * Konstruktor setzt den Titel der Liste
     */
    public TodoList(String title, String type) {
        this.title = title;
        this.type = type;
    }

    /**
     * Gibt den Titel der Liste zurück
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gibt den Typ der Liste zurück.
     */
    public String getType() {
        return type;
    }
}