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
     * Fügt eine neue Liste hinzu und speichert den neuen Zustand.
     */
    public void addList(TodoList list) {

        // Neue Liste zum Model hinzufügen.
        app.addList(list);

        // Änderung dauerhaft speichern.
        save();
    }

    /**
     * Entfernt eine Liste und speichert den neuen Zustand.
     */
    public void removeList(TodoList list) {

        // Liste aus dem Model entfernen.
        app.removeList(list);

        // Änderung dauerhaft speichern.
        save();
    }

    /**
     * Ändert den Titel einer bestehenden Todo-Liste und speichert den neuen Zustand.
     */
    public void renameList(TodoList list, String newTitle) {

        // Der neue Titel wird direkt in der übergebenen Liste gespeichert.
        list.setTitle(newTitle);

        // Änderung dauerhaft speichern.
        save();
    }

    /**
     * Fügt ein Item zu einer Checkbox-Liste hinzu und speichert den neuen Zustand.
     */
    public void addItem(CheckboxTodoList list, String text) {

        // Neues Item zur Checkbox-Liste hinzufügen.
        list.addItem(text);

        // Änderung dauerhaft speichern.
        save();
    }

    /**
     * Toggled ein Item und speichert den neuen Zustand.
     */
    public void toggleItem(CheckboxTodoList list, TodoItem item) {

        // Status des Items ändern und Liste im Model neu sortieren.
        list.toggleItem(item);

        // Änderung dauerhaft speichern.
        save();
    }

    /**
     * Ändert den Text eines bestehenden TodoItems und speichert den neuen Zustand.
     */
    public void editItem(TodoItem item, String newText) {

        // Der neue Text wird direkt im vorhandenen Item gespeichert.
        item.setText(newText);

        // Änderung dauerhaft speichern.
        save();
    }

    /**
     * Entfernt ein TodoItem aus einer Checkbox-Liste und speichert den neuen Zustand.
     */
    public void removeItem(CheckboxTodoList list, TodoItem item) {

        // Der Controller delegiert das Entfernen an das Model.
        list.removeItem(item);

        // Änderung dauerhaft speichern.
        save();
    }

    /**
     * Fügt einer Freitext-Liste einen neuen Eintrag hinzu und speichert den neuen Zustand.
     */
    public void addTextEntry(TextTodoList list, String text) {

        // Der neue Text wird an die übergebene Freitext-Liste angehängt.
        list.addEntry(text);

        // Änderung dauerhaft speichern.
        save();
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