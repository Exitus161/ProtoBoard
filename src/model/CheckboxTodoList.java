package model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Collections;

/**
 * Todo-Liste mit Checkboxen (einzelne Items).
 */
public class CheckboxTodoList extends TodoList {

    // Liste aller TodoItems
    private List<TodoItem> items;

    /**
     * Konstruktor: erstellt leere Liste
     */
    public CheckboxTodoList(String title) {
        super(title, "checkbox");
        this.items = new ArrayList<>();
    }

    /**
     * Leerer Konstruktor für Gson
     */
    public CheckboxTodoList() {
        super("", "checkbox");
        this.items = new ArrayList<>();
    }

    /**
     * Fügt ein neues TodoItem zur Liste hinzu
     */
    public void addItem(String text) {
        items.add(0, new TodoItem(text));
    }

    /**
     * Ändert den Status eines Items (erledigt / nicht erledigt)
     * und sortiert danach automatisch die Liste
     */
    public void toggleItem(TodoItem item) {
        item.toggle();
        sortItems();
    }

    /**
     * Entfernt ein TodoItem aus der Liste.
     */
    public void removeItem(TodoItem item) {

        // Das übergebene Item wird aus der internen Liste entfernt.
        items.remove(item);
    }

    /**
     * Sortiert die Liste:
     * - offene Aufgaben oben
     * - erledigte Aufgaben unten
     */
    private void sortItems() {
        items.sort(Comparator.comparing(TodoItem::isCompleted));
    }

    /**
     * Gibt alle Items zurück (für Anzeige)
     */
    public List<TodoItem> getItems() {

        // Gibt eine nicht direkt veränderbare Sicht auf die Items zurück.
        // Änderungen sollen über Methoden dieser Klasse passieren.
        return Collections.unmodifiableList(items);
    }
}