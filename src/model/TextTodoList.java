package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

/**
 * Todo-Liste für einfache Freitext-Einträge.
 * Diese Liste besitzt keine Checkboxen und keinen Abschluss-Status.
 * 
 * Diese Klasse demonstriert Vererbung und Polymorphie:
 * Sie erbt von der abstrakten Klasse {@link model.TodoList} und implementiert
 * ihre spezifische Art, Todo-Einträge zu verwalten (als einfache Text-Strings).
 * 
 * Im Gegensatz zu {@link model.CheckboxTodoList} speichert TextTodoList
 * beliebige Freitext-Einträge ohne Status-Information. Der Typ ist "text".
 * 
 * <b>Polymorphie in Aktion:</b> Dank der abstrakten Basisklasse kann die GUI
 * und der Controller mit {@code TodoList} arbeiten, ohne die konkrete Implementierung
 * zu kennen. Der {@link controller.TodoListAdapter} nutzt den Type "text" zur
 * polymorphen Deserialisierung.
 * 
 * @see model.TodoList
 * @see model.CheckboxTodoList
 * @see controller.TodoListAdapter
 */
public class TextTodoList extends TodoList {

    // Liste aller Textelemente
    private List<String> entries;

    /**
     * Konstruktor erstellt eine neue Freitext-Liste mit dem angegebenen Titel.
     * 
     * Setzt den Listentyp auf "text" und initialisiert die Einträge als leere Liste.
     * 
     * @param title Der Name der Freitext-Liste
     */
    public TextTodoList(String title) {
        super(title, "text");
        entries = new ArrayList<>();
    }

    /**
     * Leerer Konstruktor für Gson-Deserialisierung.
     * 
     * Wird von {@link controller.TodoListAdapter} aufgerufen, wenn TextTodoList-Objekte
     * aus JSON geladen werden. Gson füllt dann die Felder nach der Instanziierung.
     */
    public TextTodoList() {
        super("", "text");
        entries = new ArrayList<>();
    }

    /**
     * Stellt sicher, dass die interne Eintragsliste existiert.
     * 
     * Dies ist eine Defensivprogrammierung gegen fehlerhafte oder unvollständige
     * JSON-Daten (z.B. wenn die {@code entries}-Liste bei der Deserialisierung
     * nicht korrekt geladen wurde).
     */
    private void ensureEntriesExist() {

        // Falls die Liste durch fehlerhafte oder unvollständige JSON-Daten fehlt,
        // wird sie hier neu angelegt.
        if (entries == null) {
            entries = new ArrayList<>();
        }
    }

    /**
     * Gibt alle Einträge zurück.
     * 
     * Die Rückgabe ist eine unveränderbare (unmodifiable) View der internen Liste.
     * Dies erzwingt, dass Änderungen nur über die bereitgestellten Methoden
     * ({@link #addEntry(String)}, {@link #removeEntry(String)}, {@link #setEntries(List)})
     * erfolgen und gewährleistet Datenkonsistenz.
     * 
     * @return Eine unveränderbare Liste aller Freitext-Einträge
     */
    public List<String> getEntries() {

        // Sicherstellen, dass die interne Liste existiert.
        ensureEntriesExist();

        // Gibt eine nicht direkt veränderbare Sicht auf die Einträge zurück.
        // Änderungen sollen über addEntry(...) oder setEntries(...) passieren.
        return Collections.unmodifiableList(entries);
    }

    /**
     * Fügt einen neuen Eintrag zu dieser Freitext-Liste hinzu.
     * 
     * @param entry Der neue Freitext-Eintrag (wird ans Ende der Liste angehängt)
     */
    public void addEntry(String entry) {

        // Sicherstellen, dass die interne Liste existiert.
        ensureEntriesExist();

        entries.add(entry);
    }

    /**
     * Entfernt einen Eintrag aus dieser Freitext-Liste.
     * 
     * @param entry Der zu entfernende Eintrag
     */
    public void removeEntry(String entry) {

        // Sicherstellen, dass die interne Liste existiert.
        ensureEntriesExist();

        entries.remove(entry);
    }

    /**
     * Ersetzt alle Einträge dieser Liste durch eine neue Liste.
     * 
     * Falls {@code entries} null ist, wird die Liste geleert.
     * 
     * @param entries Die neue Liste von Freitext-Einträgen (null wird zu leerer ArrayList)
     */
    public void setEntries(List<String> entries) {

        // Falls null übergeben wird, wird stattdessen eine leere Liste gespeichert.
        if (entries == null) {
            this.entries = new ArrayList<>();
            return;
        }

        // Die übergebene Liste wird als neue interne Eintragsliste gespeichert.
        this.entries = entries;
    }
}