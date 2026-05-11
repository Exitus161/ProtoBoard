package controller;

import model.CheckboxTodoList;
import model.TodoApp;
import model.TodoItem;
import model.TodoList;

/**
 * Controller verbindet GUI mit Model.
 * Enthält keine eigene Logik, sondern steuert Abläufe.
 */
public class TodoController {

    // Referenz auf die gesamte Anwendung (alle Listen)
    private TodoApp app;

    // Verwaltet Speichern/Laden
    private PersistenceManager persistenceManager;

    /**
     * Konstruktor
     */
    public TodoController(TodoApp app, PersistenceManager persistenceManager) {
        this.app = app;
        this.persistenceManager = persistenceManager;
    }

    /**
     * Fügt eine neue Liste hinzu
     */
    public void addList(TodoList list) {
        app.addList(list);
    }

    /**
     * Entfernt eine Liste
     */
    public void removeList(TodoList list) {
        app.removeList(list);
    }

    /**
     * Fügt ein Item zu einer Checkbox-Liste hinzu
     */
    public void addItem(CheckboxTodoList list, String text) {
        list.addItem(text);
    }

    /**
     * Toggled ein Item (inkl. Sortierung im Model!)
     */
    public void toggleItem(CheckboxTodoList list, TodoItem item) {
        list.toggleItem(item);
    }

    /**
     * Ändert den Text eines bestehenden TodoItems.
     */
    public void editItem(TodoItem item, String newText) {

        // Der neue Text wird direkt im vorhandenen Item gespeichert.
        item.setText(newText);
    }

    /**
     * Entfernt ein TodoItem aus einer Checkbox-Liste.
     */
    public void removeItem(CheckboxTodoList list, TodoItem item) {

        // Das Item wird aus der Liste entfernt,
        // zu der es gehört.
        list.getItems().remove(item);
    }

    /**
     * Speichert den aktuellen Stand der App.
     */
    public void save() {
        persistenceManager.save(app);
    }

    /**
     * Gibt Zugriff auf das Model (für GUI)
     */
    public TodoApp getApp() {
        return app;
    }
}