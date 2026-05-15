package model;

/**
 * Abstrakte Oberklasse für alle Arten von Todo-Listen.
 * Enthält gemeinsame Eigenschaften wie Titel und Typ.
 * 
 * Diese Klasse demonstriert das Konzept der Vererbung und Polymorphie.
 * Sie definiert eine allgemeine Schnittstelle für verschiedene Todo-Listen-Typen,
 * die ihre spezifische Funktionalität implementieren können.
 * 
 * Konkrete Implementierungen (Unterklassen):
 * - {@link model.CheckboxTodoList}: Todo-Items mit Abschluss-Status
 * - {@link model.TextTodoList}: Freitext-Einträge
 * 
 * Das Design ermöglicht Polymorphie: Code kann mit {@code TodoList} arbeiten,
 * ohne die konkrete Implementierung zu kennen. Die {@link controller.TodoListAdapter}
 * nutzt dies zur polymorphen Deserialisierung aus JSON.
 * 
 * @see model.CheckboxTodoList
 * @see model.TextTodoList
 * @see controller.TodoListAdapter
 */
public abstract class TodoList {

    // Titel der Liste
    protected String title;
    // Typ der Liste
    protected String type;

    /**
     * Konstruktor setzt den Titel und Typ der Liste.
     * 
     * @param title Der Name der Liste (null wird zu leerer String)
     * @param type Der Typ der Liste, z.B. "checkbox" oder "text" (null wird zu leerer String)
     */
    public TodoList(String title, String type) {

        // Titel sauber speichern und null vermeiden.
        this.title = cleanTitle(title);

        // Typ sauber speichern und null vermeiden.
        this.type = cleanType(type);

    }

    /**
     * Gibt den Titel der Liste zurück.
     * 
     * @return Der Titel der Liste (nie null)
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gibt den Typ der Liste zurück.
     * 
     * @return Der Typ der Liste, z.B. "checkbox" oder "text" (nie null)
     */
    public String getType() {
        return type;
    }

    /**
     * Setzt einen neuen Titel für die Liste.
     * Der Titel wird automatisch bereinigt (null → "", Leerzeichen trimmen).
     * 
     * @param title Der neue Name der Liste (null wird zu leerer String)
     */
    public void setTitle(String title) {

        // Titel sauber speichern und null vermeiden.
        this.title = cleanTitle(title);
    }

    /**
     * Bereitet Titelwerte für TodoListen auf.
     * 
     * Konvertiert null-Werte zu leeren Strings und entfernt
     * führende sowie nachfolgende Leerzeichen.
     * 
     * @param title Der zu bereinigende Titel (darf null sein)
     * @return Der bereinigte Titel (nie null, minimal "")
     */
    private String cleanTitle(String title) {

        // Null-Werte werden als leerer Titel gespeichert,
        // damit später keine NullPointerException entsteht.
        if (title == null) {
            return "";
        }

        // Leerzeichen am Anfang und Ende entfernen.
        return title.trim();
    }

    /**
     * Bereitet Typwerte für TodoListen auf.
     * 
     * Konvertiert null-Werte zu leeren Strings und entfernt
     * führende sowie nachfolgende Leerzeichen.
     * 
     * @param type Der zu bereinigende Typ (darf null sein)
     * @return Der bereinigte Typ (nie null, minimal "")
     */
    private String cleanType(String type) {

        // Null-Werte werden als leerer Typ gespeichert,
        // damit später keine NullPointerException entsteht.
        if (type == null) {
            return "";
        }

        // Leerzeichen am Anfang und Ende entfernen.
        return type.trim();
    }
}