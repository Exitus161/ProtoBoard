package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

/**
 * Zentrale Model-Klasse der ProtoBoard Todo-Anwendung.
 * 
 * Diese Klasse verwaltet alle Todo-Listen und ist das Herzstück des MVC-Patterns.
 * Sie agiert als Container für die gesamte Datenschicht und koordiniert
 * verschiedene {@link model.TodoList}-Implementierungen.
 * 
 * <b>Polymorphie in Aktion:</b>
 * TodoApp speichert {@code List<TodoList>}, also die abstrakte Basisklasse.
 * Dadurch können Instanzen von {@link model.CheckboxTodoList} und
 * {@link model.TextTodoList} transparent gespeichert und verwaltet werden,
 * ohne dass TodoApp die konkreten Implementierungen kennen muss.
 * Der {@link controller.TodoListAdapter} nutzt dies zur polymorphen Deserialisierung.
 * 
 * <b>Architektur:</b>
 * TodoApp wird von {@link controller.TodoController} verwaltet,
 * von {@link controller.PersistenceManager} serialisiert/deserialisiert,
 * und von {@link view.GUI} angezeigt.
 * 
 * @see model.TodoList
 * @see model.CheckboxTodoList
 * @see model.TextTodoList
 * @see controller.TodoController
 * @see controller.PersistenceManager
 */
public class TodoApp {

    // Alle vorhandenen Listen (sowohl CheckboxTodoList als auch TextTodoList)
    private List<TodoList> lists;

    /**
     * Konstruktor initialisiert die App mit einer leeren Listen-Sammlung.
     */
    public TodoApp() {
        lists = new ArrayList<>();
    }

    /**
     * Stellt sicher, dass die interne Listen-Sammlung existiert.
     * 
     * Dies ist eine Defensivprogrammierung gegen fehlerhafte oder unvollständige
     * JSON-Daten (z.B. wenn die {@code lists}-Liste bei der Deserialisierung
     * nicht korrekt geladen wurde).
     */
    private void ensureListsExist() {

        // Falls die Liste durch fehlerhafte oder unvollständige JSON-Daten fehlt,
        // wird sie hier neu angelegt.
        if (lists == null) {
            lists = new ArrayList<>();
        }
    }

    /**
     * Fügt eine neue Todo-Liste zur App hinzu.
     * 
     * Akzeptiert jede Implementierung von {@link model.TodoList}
     * (z.B. CheckboxTodoList oder TextTodoList) dank Polymorphie.
     * 
     * @param list Die neue {@link model.TodoList} (kann CheckboxTodoList oder TextTodoList sein)
     */
    public void addList(TodoList list) {

        // Sicherstellen, dass die interne Listen-Sammlung existiert.
        ensureListsExist();

        lists.add(list);
    }

    /**
     * Entfernt eine Todo-Liste aus der App.
     * 
     * @param list Die zu entfernende {@link model.TodoList}
     */
    public void removeList(TodoList list) {

        // Sicherstellen, dass die interne Listen-Sammlung existiert.
        ensureListsExist();

        lists.remove(list);
    }

    /**
     * Gibt alle Todo-Listen der App zurück.
     * 
     * Die Rückgabe ist eine unveränderbare (unmodifiable) View der internen Liste.
     * Dies erzwingt, dass Änderungen nur über die bereitgestellten Methoden
     * ({@link #addList(TodoList)}, {@link #removeList(TodoList)}) erfolgen
     * und gewährleistet Datenkonsistenz und Datenschutz.
     * 
     * <b>Polymorphie:</b> Die Liste kann sowohl {@link model.CheckboxTodoList}
     * als auch {@link model.TextTodoList} Instanzen enthalten.
     * Der Aufrufer braucht sich um die konkreten Typen nicht zu kümmern.
     * 
     * @return Eine unveränderbare Liste aller {@link model.TodoList}-Objekte
     */
    public List<TodoList> getLists() {

        // Sicherstellen, dass die interne Listen-Sammlung existiert.
        ensureListsExist();

        // Gibt eine nicht direkt veränderbare Sicht auf die Listen zurück.
        // Änderungen sollen über addList(...) und removeList(...) passieren.
        return Collections.unmodifiableList(lists);
    }


}