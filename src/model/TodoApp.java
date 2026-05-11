package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

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
     * Stellt sicher, dass die interne Listen-Sammlung existiert.
     */
    private void ensureListsExist() {

        // Falls die Liste durch fehlerhafte oder unvollständige JSON-Daten fehlt,
        // wird sie hier neu angelegt.
        if (lists == null) {
            lists = new ArrayList<>();
        }
    }

    /**
     * Fügt eine neue Liste hinzu.
     */
    public void addList(TodoList list) {

        // Sicherstellen, dass die interne Listen-Sammlung existiert.
        ensureListsExist();

        lists.add(list);
    }

    /**
     * Entfernt eine Liste.
     */
    public void removeList(TodoList list) {

        // Sicherstellen, dass die interne Listen-Sammlung existiert.
        ensureListsExist();

        lists.remove(list);
    }

    /**
     * Gibt alle Listen zurück.
     */
    public List<TodoList> getLists() {

        // Sicherstellen, dass die interne Listen-Sammlung existiert.
        ensureListsExist();

        // Gibt eine nicht direkt veränderbare Sicht auf die Listen zurück.
        // Änderungen sollen über addList(...) und removeList(...) passieren.
        return Collections.unmodifiableList(lists);
    }


}