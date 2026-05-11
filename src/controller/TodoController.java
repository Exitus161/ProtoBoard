package controller;

import model.CheckboxTodoList;
import model.TodoApp;
import model.TodoItem;
import model.TodoList;
import model.TextTodoList;

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
     * Ändert den Titel einer bestehenden Todo-Liste.
     */
    public void renameList(TodoList list, String newTitle) {

        // Der neue Titel wird direkt in der übergebenen Liste gespeichert.
        list.setTitle(newTitle);
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

        // Der Controller delegiert das Entfernen an das Model.
        list.removeItem(item);
    }

    /**
     * Fügt einer Freitext-Liste einen neuen Eintrag hinzu.
     */
    public void addTextEntry(TextTodoList list, String text) {

        // Der neue Text wird an die übergebene Freitext-Liste angehängt.
        list.addEntry(text);
    }

    /**
     * Ersetzt alle Einträge einer Freitext-Liste.
     */
    public void replaceTextEntries(TextTodoList list, java.util.List<String> entries) {

        // Die vorhandenen Einträge werden durch die neue Liste ersetzt.
        list.setEntries(entries);
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