package controller;

import model.CheckboxTodoList;
import model.TodoApp;
import model.TodoItem;
import model.TodoList;
import model.TextTodoList;

/**
 * Controller verbindet GUI (View) mit dem Model (TodoApp).
 * 
 * Diese Klasse demonstriert die Controller-Rolle im MVC-Pattern:
 * - Empfängt Benutzer-Interaktionen von der GUI
 * - Validiert und verarbeitet diese Eingaben
 * - Delegiert Änderungen an das Model (TodoApp)
 * - Triggert Persistierung nach jeder Änderung
 * 
 * Der Controller enthält hauptsächlich Koordinations- und Delegationslogik,
 * nicht die eigentliche Geschäftslogik (diese liegt im Model).
 * 
 * Alle public Methoden führen nach einer Änderung automatisch {@link #save()} aus,
 * um Datenpersistenz zu gewährleisten.
 * 
 * @see model.TodoApp
 * @see controller.PersistenceManager
 * @see view.GUI
 */
public class TodoController {

    // Referenz auf die gesamte Anwendung mit allen Listen.
    private final TodoApp app;

    // Verwaltet das Speichern und Laden der App-Daten.
    private final PersistenceManager persistenceManager;

    /**
     * Konstruktor initialisiert den Controller mit seinen Abhängigkeiten.
     * 
     * Dies demonstriert das Dependency Injection Pattern:
     * Der Controller erhält seine Abhängigkeiten (Model und Persistierung) vom Aufrufer,
     * statt sie selbst zu erzeugen.
     * 
     * @param app Die {@link model.TodoApp} - das zentrale Model
     * @param persistenceManager Der {@link controller.PersistenceManager} für Datensicherung
     */
    public TodoController(TodoApp app, PersistenceManager persistenceManager) {
        this.app = app;
        this.persistenceManager = persistenceManager;
    }

    /**
     * Fügt eine neue Liste hinzu und speichert den neuen Zustand.
     * 
     * Delegiert die tatsächliche Addition an das Model.
     * Null-Validierung: ungültige Listen werden ignoriert.
     * 
     * @param list Die neue {@link model.TodoList} (CheckboxTodoList oder TextTodoList)
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
     * 
     * Delegiert das Entfernen an das Model.
     * Null-Validierung: ungültige Listen werden ignoriert.
     * 
     * @param list Die zu entfernende {@link model.TodoList}
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
     * 
     * Validierung: Leere oder null-Titel werden ignoriert.
     * 
     * @param list Die {@link model.TodoList}, deren Titel geändert wird
     * @param newTitle Der neue Titel (null oder blank wird ignoriert)
     */
    public void renameList(TodoList list, String newTitle) {

        // Ohne gültige Liste oder gültigen Titel gibt es nichts zu ändern.
        if (list == null || newTitle == null || newTitle.isBlank()) {
            return;
        }

        // Leerzeichen am Anfang und Ende entfernen.
        newTitle = newTitle.trim();

        // Der neue Titel wird direkt in der übergebenen Liste gespeichert.
        list.setTitle(newTitle);

        // Änderung dauerhaft speichern.
        save();
    }

    /**
     * Fügt ein Item zu einer Checkbox-Liste hinzu und speichert den neuen Zustand.
     * 
     * Delegiert die Addition an die {@link model.CheckboxTodoList}.
     * Validierung: Leere oder null-Texte werden ignoriert.
     * 
     * @param list Die {@link model.CheckboxTodoList}, zu der ein Item hinzugefügt wird
     * @param text Der Text des neuen Items (null oder blank wird ignoriert)
     */
    public void addItem(CheckboxTodoList list, String text) {

        // Ohne gültige Liste oder gültigen Text wird kein Item erstellt.
        if (list == null || text == null || text.isBlank()) {
            return;
        }

        // Leerzeichen am Anfang und Ende entfernen.
        text = text.trim();

        // Neues Item zur Checkbox-Liste hinzufügen.
        list.addItem(text);

        // Änderung dauerhaft speichern.
        save();
    }

    /**
     * Toggled den Status eines Items (erledigt ↔ nicht erledigt) und speichert den neuen Zustand.
     * 
     * Delegiert die Status-Änderung an die {@link model.CheckboxTodoList}.
     * Die Liste sortiert sich automatisch neu (erledigte Items nach unten).
     * 
     * @param list Die {@link model.CheckboxTodoList}, deren Item getoggelt wird
     * @param item Das zu toggelnde {@link model.TodoItem}
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
     * 
     * Delegiert die Textänderung an das Item.
     * Validierung: Leere oder null-Texte werden ignoriert.
     * 
     * @param item Das zu ändernde {@link model.TodoItem}
     * @param newText Der neue Aufgabentext (null oder blank wird ignoriert)
     */
    public void editItem(TodoItem item, String newText) {

        // Ohne gültiges Item oder gültigen Text gibt es nichts zu ändern.
        if (item == null || newText == null || newText.isBlank()) {
            return;
        }

        // Leerzeichen am Anfang und Ende entfernen.
        newText = newText.trim();

        // Der neue Text wird direkt im vorhandenen Item gespeichert.
        item.setText(newText);

        // Änderung dauerhaft speichern.
        save();
    }

    /**
     * Entfernt ein TodoItem aus einer Checkbox-Liste und speichert den neuen Zustand.
     * 
     * Delegiert das Entfernen an die {@link model.CheckboxTodoList}.
     * 
     * @param list Die {@link model.CheckboxTodoList}, aus der das Item entfernt wird
     * @param item Das zu entfernende {@link model.TodoItem}
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
     * 
     * Delegiert die Addition an die {@link model.TextTodoList}.
     * Validierung: Leere oder null-Texte werden ignoriert.
     * 
     * @param list Die {@link model.TextTodoList}, zu der ein Eintrag hinzugefügt wird
     * @param text Der neue Freitext-Eintrag (null oder blank wird ignoriert)
     */
    public void addTextEntry(TextTodoList list, String text) {

        // Ohne gültige Liste oder gültigen Text wird kein Eintrag erstellt.
        if (list == null || text == null || text.isBlank()) {
            return;
        }

        // Leerzeichen am Anfang und Ende entfernen.
        text = text.trim();

        // Der neue Text wird an die übergebene Freitext-Liste angehängt.
        list.addEntry(text);

        // Änderung dauerhaft speichern.
        save();
    }

    /**
     * Ersetzt alle Einträge einer Freitext-Liste und speichert den neuen Zustand.
     * 
     * Delegiert das Ersetzen an die {@link model.TextTodoList}.
     * Falls {@code entries} null ist, wird die Liste geleert.
     * 
     * @param list Die {@link model.TextTodoList}, deren Einträge ersetzt werden
     * @param entries Die neue Liste von Freitext-Einträgen (null wird zu leerer Liste)
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
     * Speichert den aktuellen Stand der App dauerhaft.
     * 
     * Wird nach jeder Zustandsänderung aufgerufen, um Datenpersistenz zu gewährleisten.
     */
    public void save() {
        persistenceManager.save(app);
    }

    /**
     * Gibt Zugriff auf das Model (für die GUI).
     * 
     * Die GUI braucht Lesezugriff auf die {@link model.TodoApp},
     * um die aktuelle Liste anzuzeigen.
     * 
     * @return Die {@link model.TodoApp} mit allen Listen und Items
     */
    public TodoApp getApp() {
        return app;
    }
}