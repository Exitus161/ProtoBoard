package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Verwaltet alle Todo-Listen (zentrale Klasse der App).
 */
public class TodoApp {

    // Alle vorhandenen Listen
    private List<TodoList> lists;

    /**
     * Konstruktor: initialisiert leere Liste
     */
    public TodoApp() {
        lists = new ArrayList<>();
    }

    /**
     * Fügt eine neue Liste hinzu
     */
    public void addList(TodoList list) {
        lists.add(list);
    }

    /**
     * Entfernt eine Liste
     */
    public void removeList(TodoList list) {
        lists.remove(list);
    }

    /**
     * Gibt alle Listen zurück
     */
    public List<TodoList> getLists() {
        return lists;
    }
}