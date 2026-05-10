package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

    // Speicherort der JSON-Datei
    private static final String FILE_PATH = "resources/todos.json";

    // Gson Objekt
    private Gson gson;

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

            File file = new File(FILE_PATH);

            // Datei existiert noch nicht
            if (!file.exists()) {

                // Neue leere Datei erstellen
                file.createNewFile();

                // Leere Grundstruktur speichern
                save(new TodoApp());

                System.out.println("Neue JSON-Datei erstellt.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Speichert alle App-Daten in die JSON-Datei
     */
    public void save(TodoApp app) {

        try (FileWriter writer = new FileWriter(FILE_PATH)) {

            gson.toJson(app, writer);

            System.out.println("Daten gespeichert.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Lädt die App-Daten aus der JSON-Datei
     */
    public TodoApp load() {

        try (FileReader reader = new FileReader(FILE_PATH)) {

            TodoApp app = gson.fromJson(reader, TodoApp.class);

            // Falls Datei leer ist
            if (app == null) {
                return new TodoApp();
            }

            System.out.println("Daten geladen.");

            return app;

        } catch (IOException e) {

            // Falls Datei noch nicht existiert
            return new TodoApp();
        }
    }
}