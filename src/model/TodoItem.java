package model;

/**
 * Repräsentiert ein einzelnes Todo-Element.
 * 
 * Ein TodoItem besteht aus einem Aufgabentext und einem Abschluss-Status.
 * Der Text wird automatisch bereinigt (null → "", Leerzeichen trimmen).
 * Der Status kann zwischen erledigt und nicht erledigt umgeschaltet werden.
 * 
 * @see model.CheckboxTodoList
 */
public class TodoItem {

    // Beschreibung der Aufgabe
    private String text;

    // Status: erledigt oder nicht
    private boolean completed;

    /**
     * Konstruktor: erstellt ein neues TodoItem mit Text.
     * Standard: nicht erledigt.
     * 
     * @param text Der Aufgabentext (null wird zu leerer String)
     */
    public TodoItem(String text) {

        // Text sauber speichern und null vermeiden.
        this.text = cleanText(text);

        // Neue Aufgaben sind standardmäßig nicht erledigt.
        this.completed = false;
    }


    /**
     * Wechselt den Status zwischen erledigt und nicht erledigt.
     */
    public void toggle() {
        completed = !completed;
    }

    /**
     * Gibt den Text der Aufgabe zurück.
     * 
     * @return Der Aufgabentext (nie null)
     */
    public String getText() {
        return text;
    }

    /**
     * Setzt einen neuen Text für das TodoItem.
     * Der Text wird automatisch bereinigt (null → "", Leerzeichen trimmen).
     * 
     * @param text Der neue Aufgabentext (null wird zu leerer String)
     */
    public void setText(String text) {

        // Text sauber speichern und null vermeiden.
        this.text = cleanText(text);
    }


    /**
     * Gibt zurück, ob die Aufgabe erledigt ist.
     * 
     * @return true wenn erledigt, false wenn noch offen
     */
    public boolean isCompleted() {
        return completed;
    }

    /**
     * Bereitet Textwerte für TodoItems auf.
     * 
     * Konvertiert null-Werte zu leeren Strings und entfernt
     * führende sowie nachfolgende Leerzeichen.
     * 
     * @param text Der zu bereinigende Text (darf null sein)
     * @return Der bereinigte Text (nie null, minimal "")
     */
    private String cleanText(String text) {

        // Null-Werte werden als leerer Text gespeichert,
        // damit später keine NullPointerException entsteht.
        if (text == null) {
            return "";
        }

        // Leerzeichen am Anfang und Ende entfernen.
        return text.trim();
    }
}