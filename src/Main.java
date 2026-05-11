import controller.PersistenceManager;
import controller.TodoController;
import model.TodoApp;
import view.GUI;

import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {

        // Persistenzmanager erstellen.
        PersistenceManager persistence = new PersistenceManager();

        // Gespeicherte Daten laden.
        TodoApp app = persistence.load();

        // Controller erstellen, der Model und Persistenz verbindet.
        TodoController controller = new TodoController(app, persistence);

        // Beim Beenden automatisch den letzten Stand speichern.
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {

            // Aktuellen Zustand der App dauerhaft sichern.
            persistence.save(app);
        }));

        // Startet die Swing-Oberfläche auf dem dafür vorgesehenen UI-Thread.
        SwingUtilities.invokeLater(() -> {

            // GUI starten.
            new GUI(controller);
        });
    }
}