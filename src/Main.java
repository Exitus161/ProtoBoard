import controller.PersistenceManager;
import controller.TodoController;
import model.TodoApp;
import view.GUI;

public class Main {

    public static void main(String[] args) {

        // Persistenzmanager erstellen
        PersistenceManager persistence = new PersistenceManager();

        // Gespeicherte Daten laden
        TodoApp app = persistence.load();

        // Controller erstellen
        TodoController controller = new TodoController(app, persistence);

        // GUI starten
        new GUI(controller);

        // Beim Beenden automatisch speichern
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            persistence.save(app);
        }));
    }
}