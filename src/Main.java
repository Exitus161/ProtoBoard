import controller.PersistenceManager;
import controller.TodoController;
import model.TodoApp;
import view.GUI;

//hallo
//moin

import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {

        // Startet die Swing-Oberfläche auf dem dafür vorgesehenen UI-Thread.
        SwingUtilities.invokeLater(() -> {

            // Persistenzmanager erstellen.
            PersistenceManager persistence = new PersistenceManager();

            // Gespeicherte Daten laden.
            TodoApp app = persistence.load();

            // Controller erstellen, der Model und Persistenz verbindet.
            TodoController controller = new TodoController(app, persistence);

            // GUI starten.
            new GUI(controller);

            // Beim Beenden automatisch den letzten Stand speichern.
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                persistence.save(app);
            }));
        });
    }
}