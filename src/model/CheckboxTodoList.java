package model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Collections;

/**
 * Todo-Liste mit Checkboxen (einzelne Items mit Abschluss-Status).
 * 
 * Diese Klasse demonstriert Vererbung und Polymorphie:
 * Sie erbt von der abstrakten Klasse {@link model.TodoList} und implementiert
 * ihre spezifische Art, Todo-Einträge zu verwalten (als {@link model.TodoItem}-Objekte
 * mit Abschluss-Status).
 * 
 * Im Gegensatz zu {@link model.TextTodoList} speichert CheckboxTodoList strukturierte
 * Items mit Status-Information. Der Typ ist "checkbox".
 * 
 * <b>Besonderheit: Automatische Sortierung</b>
 * Nach dem Umschalten des Status eines Items wird die Liste automatisch neu sortiert:
 * Offene Aufgaben nach oben, erledigte Aufgaben nach unten. Dies ermöglicht
 * eine intuitivere Benutzeroberfläche ohne expliziten Sortierbefehl.
 * 
 * <b>Polymorphie in Aktion:</b> Dank der abstrakten Basisklasse kann die GUI
 * und der Controller mit {@code TodoList} arbeiten, ohne die konkrete Implementierung
 * zu kennen. Der {@link controller.TodoListAdapter} nutzt den Type "checkbox" zur
 * polymorphen Deserialisierung.
 * 
 * @see model.TodoList
 * @see model.TodoItem
 * @see model.TextTodoList
 * @see controller.TodoListAdapter
 */
public class CheckboxTodoList extends TodoList {

    // Liste aller TodoItems
    private List<TodoItem> items;

    /**
     * Konstruktor erstellt eine neue Checkbox-Liste mit dem angegebenen Titel.
     * 
     * Setzt den Listentyp auf "checkbox" und initialisiert die Items als leere Liste.
     * 
     * @param title Der Name der Checkbox-Liste
     */
    public CheckboxTodoList(String title) {
        super(title, "checkbox");
        this.items = new ArrayList<>();
    }

    /**
     * Leerer Konstruktor für Gson-Deserialisierung.
     * 
     * Wird von {@link controller.TodoListAdapter} aufgerufen, wenn CheckboxTodoList-Objekte
     * aus JSON geladen werden. Gson füllt dann die Felder nach der Instanziierung.
     */
    public CheckboxTodoList() {
        super("", "checkbox");
        this.items = new ArrayList<>();
    }

    /**
     * Stellt sicher, dass die interne Item-Liste existiert.
     * 
     * Dies ist eine Defensivprogrammierung gegen fehlerhafte oder unvollständige
     * JSON-Daten (z.B. wenn die {@code items}-Liste bei der Deserialisierung
     * nicht korrekt geladen wurde).
     */
    private void ensureItemsExist() {

        // Falls die Liste durch fehlerhafte oder unvollständige JSON-Daten fehlt,
        // wird sie hier neu angelegt.
        if (items == null) {
            items = new ArrayList<>();
        }
    }

    /**
     * Fügt ein neues TodoItem zur Liste hinzu.
     * 
     * Das neue Item wird an Position 0 (Anfang der Liste) eingefügt,
     * sodass neu erstellte Aufgaben immer oben stehen.
     * 
     * @param text Der Text des neuen Items
     */
    public void addItem(String text) {

        // Sicherstellen, dass die interne Liste existiert.
        ensureItemsExist();

        items.add(0, new TodoItem(text));
    }

    /**
     * Toggelt den Status eines Items (erledigt ↔ nicht erledigt)
     * und sortiert die Liste automatisch neu.
     * 
     * Nach dem Statuswechsel werden offene Aufgaben nach oben
     * und erledigte nach unten verschoben für bessere Übersichtlichkeit.
     * 
     * @param item Das zu toggelnde {@link model.TodoItem}
     */
    public void toggleItem(TodoItem item) {
        item.toggle();
        sortItems();
    }

    /**
     * Entfernt ein TodoItem aus der Liste.
     * 
     * @param item Das zu entfernende {@link model.TodoItem}
     */
    public void removeItem(TodoItem item) {

        // Sicherstellen, dass die interne Liste existiert.
        ensureItemsExist();

        items.remove(item);
    }

    /**
     * Sortiert die Liste nach Status.
     * 
     * Sortierkriterium: Offene Aufgaben oben, erledigte Aufgaben unten.
     * Dies wird mit {@link java.util.Comparator#comparing(java.util.function.Function)}
     * und einer Method Reference ({@code TodoItem::isCompleted}) implementiert.
     * 
     * <b>Java-Konzept: Method References</b>
     * {@code Comparator.comparing(TodoItem::isCompleted)} ist eine kompakte Schreibweise für:
     * {@code Comparator.comparing((TodoItem item) -> item.isCompleted())}
     */
    private void sortItems() {

        // Sicherstellen, dass die interne Liste existiert.
        ensureItemsExist();

        // Offene Aufgaben sollen oben stehen,
        // erledigte Aufgaben darunter.
        items.sort(Comparator.comparing(TodoItem::isCompleted));
    }


    /**
     * Gibt alle Items dieser Liste zurück.
     * 
     * Die Rückgabe ist eine unveränderbare (unmodifiable) View der internen Liste.
     * Dies erzwingt, dass Änderungen nur über die bereitgestellten Methoden
     * ({@link #addItem(String)}, {@link #toggleItem(TodoItem)}, {@link #removeItem(TodoItem)})
     * erfolgen und gewährleistet Datenkonsistenz.
     * 
     * @return Eine unveränderbare Liste aller {@link model.TodoItem}-Objekte
     */
    public List<TodoItem> getItems() {

        // Sicherstellen, dass die interne Liste existiert.
        ensureItemsExist();

        // Gibt eine nicht direkt veränderbare Sicht auf die Items zurück.
        // Änderungen sollen über Methoden dieser Klasse passieren.
        return Collections.unmodifiableList(items);
    }

}