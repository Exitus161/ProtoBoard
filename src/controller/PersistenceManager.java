package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

import model.TodoApp;
import model.TodoList;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;

/**
 * Verwaltet das Speichern und Laden der App-Daten.
 */
public class PersistenceManager {

    // Ordner, in dem die JSON-Datei im Projekt gespeichert wird.
    private static final String RESOURCE_DIRECTORY = "resources";

    // Name der JSON-Datei mit den gespeicherten Todo-Daten.
    private static final String FILE_NAME = "todos.json";

    // Vollständiger Speicherpfad innerhalb des Projekts.
    private static final String FILE_PATH = RESOURCE_DIRECTORY + File.separator + FILE_NAME;

    // Gson-Objekt für die JSON-Umwandlung.
    private final Gson gson;

    /**
     * Konstruktor
     */
    public PersistenceManager() {

        gson = new GsonBuilder()
                .registerTypeAdapter(TodoList.class, new TodoListAdapter())
                .setPrettyPrinting()
                .create();

        createFileIfNeeded();
    }

    /**
     * Erstellt die JSON-Datei automatisch,
     * falls sie noch nicht existiert.
     */
    private void createFileIfNeeded() {

        try {

            // Dateiobjekt für den Speicherpfad erstellen.
            File file = new File(FILE_PATH);

            // Elternordner der Datei ermitteln.
            // Bei "resources/todos.json" ist das der Ordner "resources".
            File parentDirectory = file.getParentFile();

            // Falls der Elternordner existieren sollte, aber noch fehlt,
            // wird er hier automatisch angelegt.
            if (parentDirectory != null && !parentDirectory.exists()) {
                parentDirectory.mkdirs();
            }

            // Wenn die Datei noch nicht existiert,
            // wird eine neue Datei mit leerer App-Struktur erstellt.
            if (!file.exists()) {

                file.createNewFile();

                // Leere Grundstruktur speichern.
                save(new TodoApp());

                System.out.println("New JSON file created.");
            }

        } catch (IOException e) {

            // Erstellen der Speicherdatei ist fehlgeschlagen.
            // Die Fehlermeldung wird in der Konsole ausgegeben.
            System.err.println("Storage file could not be created.");
            System.err.println("Cause: " + e.getMessage());
        }
    }

    /**
     * Speichert alle App-Daten in die JSON-Datei
     */
    public void save(TodoApp app) {

        try (FileWriter writer = new FileWriter(FILE_PATH)) {

            gson.toJson(app, writer);

            System.out.println("Todo data saved.");

        } catch (IOException e) {

        // Speichern ist fehlgeschlagen.
        // Die Fehlermeldung wird in der Konsole ausgegeben.
        System.err.println("Todo data could not be saved.");
        System.err.println("Cause: " + e.getMessage());
    }
}

    /**
     * Lädt die App-Daten aus der JSON-Datei.
     * Falls die Datei fehlt, leer oder beschädigt ist,
     * wird eine neue leere TodoApp zurückgegeben.
     */
    public TodoApp load() {

        try (FileReader reader = new FileReader(FILE_PATH)) {

            // JSON-Datei in ein TodoApp-Objekt umwandeln.
            TodoApp app = gson.fromJson(reader, TodoApp.class);

            // Wenn die Datei leer ist, liefert Gson null zurück.
            // In diesem Fall starten wir mit einer leeren App.
            if (app == null) {
                return new TodoApp();
            }

            System.out.println("Todo data loaded.");

            return app;

        } catch (IOException | JsonParseException e) {

            // Lesen oder Umwandeln der Datei ist fehlgeschlagen.
            // Die App startet trotzdem mit einem leeren Zustand.
            System.err.println("Todo data could not be loaded.");
            System.err.println("Starting with an empty todo app.");
            System.err.println("Cause: " + e.getMessage());

            return new TodoApp();
        }
    }
}