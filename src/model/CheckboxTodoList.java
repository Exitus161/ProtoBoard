package model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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
        items.add(new TodoItem(text));
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
        return items;
    }
}