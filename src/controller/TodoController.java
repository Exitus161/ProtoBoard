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

    // Referenz auf die gesamte Anwendung mit allen Listen.
    private final TodoApp app;

    // Verwaltet das Speichern und Laden der App-Daten.
    private final PersistenceManager persistenceManager;

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

        // Ungültige Listen werden nicht hinzugefügt.
        if (list == null) {
            return;
        }

        // Neue Liste zum Model hinzufügen.
        app.addList(list);

        // Änderung dauerhaft speichern.
        save();
    }

    /**
     * Entfernt eine Liste und speichert den neuen Zustand.
     */
    public void removeList(TodoList list) {

        // Ohne gültige Liste gibt es nichts zu entfernen.
        if (list == null) {
            return;
        }

        // Liste aus dem Model entfernen.
        app.removeList(list);

        // Änderung dauerhaft speichern.
        save();
    }

    /**
     * Ändert den Titel einer bestehenden Todo-Liste und speichert den neuen Zustand.
     */
    public void renameList(TodoList list, String newTitle) {

        // Ohne gültige Liste oder gültigen Titel gibt es nichts zu ändern.
        if (list == null || newTitle == null || newTitle.isBlank()) {
            return;
        }

        // Der neue Titel wird direkt in der übergebenen Liste gespeichert.
        list.setTitle(newTitle);

        // Änderung dauerhaft speichern.
        save();
    }

    /**
     * Fügt ein Item zu einer Checkbox-Liste hinzu und speichert den neuen Zustand.
     */
    public void addItem(CheckboxTodoList list, String text) {

        // Ohne gültige Liste oder gültigen Text wird kein Item erstellt.
        if (list == null || text == null || text.isBlank()) {
            return;
        }

        // Neues Item zur Checkbox-Liste hinzufügen.
        list.addItem(text);

        // Änderung dauerhaft speichern.
        save();
    }

    /**
     * Toggled ein Item und speichert den neuen Zustand.
     */
    public void toggleItem(CheckboxTodoList list, TodoItem item) {

        // Ohne gültige Liste oder gültiges Item gibt es nichts zu ändern.
        if (list == null || item == null) {
            return;
        }

        // Status des Items ändern und Liste im Model neu sortieren.
        list.toggleItem(item);

        // Änderung dauerhaft speichern.
        save();
    }

    /**
     * Ändert den Text eines bestehenden TodoItems und speichert den neuen Zustand.
     */
    public void editItem(TodoItem item, String newText) {

        // Ohne gültiges Item oder gültigen Text gibt es nichts zu ändern.
        if (item == null || newText == null || newText.isBlank()) {
            return;
        }

        // Der neue Text wird direkt im vorhandenen Item gespeichert.
        item.setText(newText);

        // Änderung dauerhaft speichern.
        save();
    }

    /**
     * Entfernt ein TodoItem aus einer Checkbox-Liste und speichert den neuen Zustand.
     */
    public void removeItem(CheckboxTodoList list, TodoItem item) {

        // Ohne gültige Liste oder gültiges Item gibt es nichts zu entfernen.
        if (list == null || item == null) {
            return;
        }

        // Der Controller delegiert das Entfernen an das Model.
        list.removeItem(item);

        // Änderung dauerhaft speichern.
        save();
    }

    /**
     * Fügt einer Freitext-Liste einen neuen Eintrag hinzu und speichert den neuen Zustand.
     */
    public void addTextEntry(TextTodoList list, String text) {

        // Ohne gültige Liste oder gültigen Text wird kein Eintrag erstellt.
        if (list == null || text == null || text.isBlank()) {
            return;
        }

        // Der neue Text wird an die übergebene Freitext-Liste angehängt.
        list.addEntry(text);

        // Änderung dauerhaft speichern.
        save();
    }

    /**
     * Ersetzt alle Einträge einer Freitext-Liste.
     */
    public void replaceTextEntries(TextTodoList list, java.util.List<String> entries) {

        // Ohne gültige Liste gibt es nichts zu ersetzen.
        if (list == null) {
            return;
        }

        // Die vorhandenen Einträge werden durch die neue Liste ersetzt.
        // Falls entries null ist, kümmert sich das Model um eine leere Liste.
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