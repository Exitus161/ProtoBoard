package model;

/**
 * Repräsentiert ein einzelnes Todo-Element.
 */
public class TodoItem {

    // Beschreibung der Aufgabe
    private String text;

    // Status: erledigt oder nicht
    private boolean completed;

    /**
     * Konstruktor: erstellt ein neues TodoItem mit Text
     * Standard: nicht erledigt
     */
    public TodoItem(String text) {
        this.text = text;
        this.completed = false;
    }

    /**
     * Wechselt den Status (erledigt <-> nicht erledigt)
     */
    public void toggle() {
        completed = !completed;
    }

    /**
     * Gibt den Text der Aufgabe zurück
     */
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    /**
     * Gibt zurück, ob die Aufgabe erledigt ist
     */
    public boolean isCompleted() {
        return completed;
    }
}