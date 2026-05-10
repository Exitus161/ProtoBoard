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

    /**
     * Konstruktor
     */
    public TodoController(TodoApp app) {
        this.app = app;
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
     * Gibt Zugriff auf das Model (für GUI)
     */
    public TodoApp getApp() {
        return app;
    }
}