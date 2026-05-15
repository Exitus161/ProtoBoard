import controller.PersistenceManager;
import controller.TodoController;
import model.TodoApp;
import view.GUI;

import javax.swing.SwingUtilities;

/**
 * Entry Point der ProtoBoard Todo-Anwendung.
 * 
 * Diese Klasse demonstriert das Model-View-Controller (MVC) Pattern mit 3-Schichten-Architektur.
 * Sie koordiniert die Initialisierung aller Komponenten in der korrekten Reihenfolge:
 * 
 * Datenpersistenz (Laden) → Model (Daten) → Controller (Logik über Dependency Injection) → 
 * Lifecycle-Management → View (GUI)
 * 
 * Das Design zeigt wichtige Java-Konzepte:
 * - Separation of Concerns: Klare Trennung von Model, View und Controller
 * - Dependency Injection: Der Controller erhält seine Abhängigkeiten (Model und Persistierung)
 * - Datenpersistenz: Automatisches Laden beim Start und Speichern beim Beenden
 * - Thread-Management: GUI läuft auf eigenem Swing-Event-Dispatch-Thread
 * 
 * @see model.TodoApp
 * @see controller.TodoController
 * @see controller.PersistenceManager
 * @see view.GUI
 */
public class Main {

    /**
     * Startet die ProtoBoard Todo-Anwendung.
     * 
     * Initialisiert alle Komponenten nach dem MVC-Pattern:
     * 1. Persistierung erstellen und Daten laden
     * 2. Controller mit Model und Persistierung via Dependency Injection instantiieren
     * 3. Shutdown Hook registrieren für Datensicherheit beim Beenden
     * 4. GUI auf dem Swing-Event-Thread starten
     * 
     * @param args Kommandozeilen-Argumente (nicht genutzt)
     */
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