package view;

import controller.TodoController;
import model.TextTodoList;
import model.CheckboxTodoList;
import model.TodoApp;
import model.TodoItem;
import model.TodoList;

import javax.swing.*;
import java.awt.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Haupt-GUI der Todo-App
 */
public class GUI {

    // Controller, über den die GUI Änderungen am Model ausführt.
    private final TodoController controller;

    // App-Model, aus dem die GUI Daten zur Anzeige liest.
    private final TodoApp app;

    // Aktuell ausgewählte Liste
    private TodoList currentList;

    // Swing Komponenten
    private JFrame frame;

    // Linke Seitenleiste
    private JList<String> listOverview;
    private DefaultListModel<String> listModel;

    // Rechte Seite mit TodoItems
    private JPanel todoPanel;

    // Titel der aktuell ausgewählten Liste
    private JLabel currentListLabel;

    // Eingabefeld für Textlisten
    private JTextArea textArea;

    // Button zum Hinzufügen neuer Einträge.
    private JButton addItemButton;

    /**
     * Konstruktor
     */
    public GUI(TodoController controller) {
        this.controller = controller;
        this.app = controller.getApp();

        init();
    }

    /**
     * Initialisiert die GUI
     */
    private void init() {

        frame = new JFrame("Todo App");
        frame.setSize(700, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // -----------------------------
        // LINKE SEITE (Listenübersicht)
        // -----------------------------

        listModel = new DefaultListModel<>();
        listOverview = new JList<>(listModel);

        // Rechtsklick-Menü für Listen
        JPopupMenu popupMenu = new JPopupMenu();

        JMenuItem deleteItem = new JMenuItem("Delete List");
        JMenuItem renameItem = new JMenuItem("Rename List");

        deleteItem.addActionListener(e -> {

            int selectedIndex = listOverview.getSelectedIndex();

            if (selectedIndex >= 0) {

                // Die aktuell ausgewählte Liste wird anhand des Index geholt.
                TodoList list = app.getLists().get(selectedIndex);

                // Vor dem Löschen nachfragen, damit Listen nicht versehentlich entfernt werden.
                int confirm = JOptionPane.showConfirmDialog(
                        frame,
                        "Delete list \"" + list.getTitle() + "\"?",
                        "Delete List",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

                // Wenn der Benutzer nicht bestätigt,
                // wird die Liste nicht gelöscht.
                if (confirm != JOptionPane.YES_OPTION) {
                    return;
                }

                // Die Liste wird über den Controller entfernt.
                controller.removeList(list);

                // Linke Listenübersicht nach dem Löschen aktualisieren.
                refreshListOverview();

                // Wenn nach dem Löschen noch Listen vorhanden sind,
                // wird automatisch eine sinnvolle nächste Liste ausgewählt.
                if (!app.getLists().isEmpty()) {

                    // Wenn die letzte Liste gelöscht wurde,
                    // nehmen wir den neuen letzten Index.
                    int nextIndex = Math.min(selectedIndex, app.getLists().size() - 1);

                    // Die nächste Liste wird links ausgewählt.
                    // Dadurch wird auch der ListSelectionListener ausgelöst.
                    listOverview.setSelectedIndex(nextIndex);

                } else {

                    // Wenn keine Liste mehr vorhanden ist,
                    // gibt es auch keine aktuelle Liste mehr.
                    currentList = null;

                    // Rechte Ansicht auf "Keine Liste ausgewählt" zurücksetzen.
                    refreshTodoPanel();
                }
            }
        });

        renameItem.addActionListener(e -> {

            int selectedIndex = listOverview.getSelectedIndex();

            if (selectedIndex >= 0) {

                TodoList list = app.getLists().get(selectedIndex);

                String newTitle = (String) JOptionPane.showInputDialog(
                        frame,
                        "New list name:",
                        "Rename List",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        null,
                        list.getTitle()
                );

                if (newTitle != null && !newTitle.isBlank()) {

                    // Leerzeichen am Anfang und Ende entfernen,
                    // damit der neue Listenname sauber gespeichert wird.
                    newTitle = newTitle.trim();

                    // Die GUI benennt die Liste nicht direkt um,
                    // sondern gibt die Änderung an den Controller weiter.
                    controller.renameList(list, newTitle);

                    refreshListOverview();
                    refreshTodoPanel();
                }
            }
        });

        popupMenu.add(deleteItem);
        popupMenu.add(renameItem);

        // Vor dem Anzeigen des Rechtsklick-Menüs wird die Liste ausgewählt,
        // auf die der Benutzer tatsächlich geklickt hat.
        popupMenu.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {

            @Override
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent e) {

                // Mausposition innerhalb der Listenübersicht holen.
                Point mousePosition = listOverview.getMousePosition();

                // Falls keine Mausposition verfügbar ist,
                // kann keine Liste gezielt ausgewählt werden.
                if (mousePosition == null) {
                    return;
                }

                // Index der Liste berechnen, die sich unter der Maus befindet.
                int index = listOverview.locationToIndex(mousePosition);

                // Wenn kein gültiger Index gefunden wurde,
                // wird keine Liste ausgewählt.
                if (index < 0) {
                    return;
                }

                // Bereich der gefundenen Listenzelle ermitteln.
                Rectangle cellBounds = listOverview.getCellBounds(index, index);

                // Wenn der Klick nicht wirklich innerhalb dieser Zelle liegt,
                // wurde wahrscheinlich in einen leeren Bereich geklickt.
                if (cellBounds == null || !cellBounds.contains(mousePosition)) {
                    return;
                }

                // Nur wenn der Klick wirklich auf einer Liste lag,
                // wird diese Liste ausgewählt.
                listOverview.setSelectedIndex(index);

            }

            @Override
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent e) {

                // Nicht benötigt.
            }

            @Override
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent e) {

                // Nicht benötigt.
            }
        });

        // Rechtsklick aktivieren
        listOverview.setComponentPopupMenu(popupMenu);

        JScrollPane leftScrollPane = new JScrollPane(listOverview);

        // Button: neue Liste erstellen
        JButton addListButton = new JButton("Create List");

        addListButton.addActionListener(e -> createNewList());

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(leftScrollPane, BorderLayout.CENTER);
        leftPanel.add(addListButton, BorderLayout.SOUTH);

        leftPanel.setPreferredSize(new Dimension(200, 500));

        // -----------------------------
        // RECHTE SEITE (TodoItems)
        // -----------------------------

        // Panel für Inhalte
        todoPanel = new JPanel();
        todoPanel.setLayout(new BoxLayout(todoPanel, BoxLayout.Y_AXIS));

        JScrollPane rightScrollPane = new JScrollPane(todoPanel);

        // Titel der aktuellen Liste
        currentListLabel = new JLabel("No list selected");
        currentListLabel.setFont(new Font("Arial", Font.BOLD, 20));

        // Button zum Hinzufügen
        addItemButton = new JButton("Add Task");

        addItemButton.addActionListener(e -> addNewItem());

        // Oberes Panel mit Titel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(currentListLabel, BorderLayout.WEST);

        // Rechte Hauptseite
        JPanel rightPanel = new JPanel(new BorderLayout());

        rightPanel.add(topPanel, BorderLayout.NORTH);
        rightPanel.add(rightScrollPane, BorderLayout.CENTER);
        rightPanel.add(addItemButton, BorderLayout.SOUTH);

        // -----------------------------
        // LISTENAUSWAHL
        // -----------------------------

        listOverview.addListSelectionListener(e -> {

            if (!e.getValueIsAdjusting()) {

                // Vor dem Wechsel der Liste wird der aktuelle Stand gespeichert.
                // Das ist besonders für Freitext-Listen wichtig.
                controller.save();

                int selectedIndex = listOverview.getSelectedIndex();

                if (selectedIndex >= 0) {

                    TodoList selectedList = app.getLists().get(selectedIndex);

                    currentList = selectedList;
                    refreshTodoPanel();
                }
            }
        });


        // -----------------------------
        // FRAME ZUSAMMENSETZEN
        // -----------------------------

        frame.add(leftPanel, BorderLayout.WEST);
        frame.add(rightPanel, BorderLayout.CENTER);

        refreshListOverview();

        refreshTodoPanel();

        frame.setVisible(true);
    }

    /**
     * Erstellt eine neue Liste
     */
    private void createNewList() {

        String title = JOptionPane.showInputDialog(
                frame,
                "List name:",
                "Create List",
                JOptionPane.QUESTION_MESSAGE
        );

        // Wenn der Benutzer abbricht oder nur Leerzeichen eingibt,
        // wird keine Liste erstellt.
        if (title == null || title.isBlank()) {
            return;
        }

        // Leerzeichen am Anfang und Ende entfernen,
        // damit der Listenname sauber gespeichert wird.
        title = title.trim();

        // Auswahl des Listentyps
        String[] options = {"Checkbox List", "Text List"};

        int choice = JOptionPane.showOptionDialog(
                frame,
                "Which type of list do you want to create?",
                "New list",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        // Wenn der Benutzer den Dialog abbricht oder schließt,
        // wird keine neue Liste erstellt.
        if (choice == JOptionPane.CLOSED_OPTION) {
            return;
        }

        TodoList newList;

        // Wenn die erste Option gewählt wurde,
        // erstellen wir eine Checkbox-Liste.
        if (choice == 0) {

            newList = new CheckboxTodoList(title);

        } else {

            // Wenn die zweite Option gewählt wurde,
            // erstellen wir eine Freitext-Liste.
            newList = new TextTodoList(title);
        }

        // Neue Liste über den Controller zum Model hinzufügen.
        controller.addList(newList);

        // Die neu erstellte Liste wird direkt als aktuelle Liste gesetzt.
        currentList = newList;

        // Linke Listenübersicht aktualisieren.
        refreshListOverview();

        // Die neue Liste in der Listenübersicht auswählen.
        listOverview.setSelectedIndex(app.getLists().indexOf(newList));

        // Rechte Todo-Ansicht aktualisieren.
        refreshTodoPanel();
    }

    /**
     * Fügt ein neues TodoItem hinzu
     */
    private void addNewItem() {

        if (currentList == null) {
            return;
        }

        String text = JOptionPane.showInputDialog(
                frame,
                "New task:",
                "Add Task",
                JOptionPane.QUESTION_MESSAGE
        );

        // Wenn der Benutzer abbricht oder nur Leerzeichen eingibt,
        // wird kein neuer Eintrag erstellt.
        if (text == null || text.isBlank()) {
            return;
        }

        // Leerzeichen am Anfang und Ende entfernen,
        // damit der Eintrag sauber gespeichert wird.
        text = text.trim();


        // -----------------------------------
        // Checkbox-Liste
        // -----------------------------------

        if (currentList instanceof CheckboxTodoList checkboxList) {

            controller.addItem(checkboxList, text);
        }

        // -----------------------------------
        // Text-Liste
        // -----------------------------------

        else if (currentList instanceof TextTodoList textList) {

            // Textlisten werden direkt im Textfeld bearbeitet.
            addItemButton.setEnabled(false);

            // Die GUI fügt den Eintrag nicht direkt hinzu,
            // sondern gibt die Aktion an den Controller weiter.
            controller.addTextEntry(textList, text);
        }

        refreshTodoPanel();
    }

    /**
     * Aktualisiert die linke Listenübersicht
     */
    private void refreshListOverview() {

        listModel.clear();

        for (TodoList list : app.getLists()) {
            listModel.addElement(list.getTitle());
        }
    }

    /**
     * Aktualisiert die rechte Todo-Ansicht
     */
    private void refreshTodoPanel() {

        todoPanel.removeAll();

        // Keine Liste ausgewählt
        if (currentList == null) {

            currentListLabel.setText("No list selected");

            // Ohne ausgewählte Liste kann kein neuer Eintrag hinzugefügt werden.
            addItemButton.setEnabled(false);

            todoPanel.revalidate();
            todoPanel.repaint();

            return;
        }

        // Titel der aktuellen Liste anzeigen
        currentListLabel.setText(currentList.getTitle());

        // -----------------------------------
        // Checkbox-Liste anzeigen
        // -----------------------------------

        if (currentList instanceof CheckboxTodoList checkboxList) {

            // Bei Checkbox-Listen werden neue Einträge über den Button hinzugefügt.
            addItemButton.setEnabled(true);

            // Layout für Checkbox-Liste zurücksetzen
            todoPanel.removeAll();
            todoPanel.setLayout(new BoxLayout(todoPanel, BoxLayout.Y_AXIS));

            // Wenn die Checkbox-Liste leer ist,
            // wird ein kurzer Hinweis angezeigt.
            if (checkboxList.getItems().isEmpty()) {

                JLabel emptyLabel = new JLabel("No tasks yet");
                emptyLabel.setForeground(Color.GRAY);
                emptyLabel.setBorder(BorderFactory.createEmptyBorder(12, 8, 12, 8));

                todoPanel.add(emptyLabel);
            }

            for (TodoItem item : checkboxList.getItems()) {

                JCheckBox checkBox = new JCheckBox(item.getText());

                // Hinweis anzeigen, wenn der Benutzer mit der Maus über der Aufgabe bleibt.
                checkBox.setToolTipText("Right-click to edit or delete");

                checkBox.setSelected(item.isCompleted());

                // Erledigte Tasks hervorheben
                if (item.isCompleted()) {

                    checkBox.setForeground(Color.GRAY);

                    Font oldFont = checkBox.getFont();

                    checkBox.setFont(
                            oldFont.deriveFont(
                                    oldFont.getStyle() | Font.ITALIC
                            )
                    );
                }

                // --------------------
                // Rechtsklick
                // --------------------

                JPopupMenu popupMenu = new JPopupMenu();

                JMenuItem editItem = new JMenuItem("Edit");
                JMenuItem deleteItem = new JMenuItem("Delete");

                popupMenu.add(editItem);
                popupMenu.add(deleteItem);

                checkBox.setComponentPopupMenu(popupMenu);

                // Edit
                editItem.addActionListener(e -> {

                    String newText = (String) JOptionPane.showInputDialog(
                            frame,
                            "Edit task:",
                            "Edit Task",
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            null,
                            item.getText()
                    );

                    if (newText != null && !newText.isBlank()) {

                        // Leerzeichen am Anfang und Ende entfernen,
                        // damit der bearbeitete Eintrag sauber gespeichert wird.
                        newText = newText.trim();

                        // Die GUI ändert das Item nicht direkt,
                        // sondern gibt die Änderung an den Controller weiter.
                        controller.editItem(item, newText);

                        refreshTodoPanel();
                    }

                });

                // Delete
                deleteItem.addActionListener(e -> {

                    // Vor dem Löschen nachfragen, damit Aufgaben nicht versehentlich entfernt werden.
                    int confirm = JOptionPane.showConfirmDialog(
                            frame,
                            "Delete task \"" + item.getText() + "\"?",
                            "Delete Task",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE
                    );

                    // Wenn der Benutzer nicht bestätigt,
                    // wird die Aufgabe nicht gelöscht.
                    if (confirm != JOptionPane.YES_OPTION) {
                        return;
                    }

                    // Die GUI löscht das Item nicht direkt,
                    // sondern gibt den Löschwunsch an den Controller weiter.
                    controller.removeItem(checkboxList, item);

                    refreshTodoPanel();
                });

                // Checkbox Event
                checkBox.addActionListener(e -> {

                    controller.toggleItem(checkboxList, item);

                    refreshTodoPanel();
                });

                // Etwas Abstand um die Checkbox setzen.
                checkBox.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

                // Die Checkbox soll nur so hoch sein wie ihr Inhalt.
                checkBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, checkBox.getPreferredSize().height + 8));

                // Checkbox direkt zur Todo-Ansicht hinzufügen.
                todoPanel.add(checkBox);
            }
        }

        // -----------------------------------
        // Text-Liste anzeigen
        // -----------------------------------

        else if (currentList instanceof TextTodoList textList) {

            textArea = new JTextArea();

            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);

            // Hinweis anzeigen, wenn der Benutzer mit der Maus über dem Textfeld bleibt.
            textArea.setToolTipText("Write one note per line");

            // Vorhandene Einträge anzeigen
            StringBuilder builder = new StringBuilder();

            for (String entry : textList.getEntries()) {
                builder.append(entry).append("\n");
            }

            textArea.setText(builder.toString());

            // Änderungen im Textfeld überwachen
            textArea.getDocument().addDocumentListener(new DocumentListener() {

                /**
                 * Aktualisiert die Einträge im Model.
                 */
                private void updateEntries() {

                    // Gesamten Text holen
                    String text = textArea.getText();

                    // Nach Zeilen trennen
                    String[] lines = text.split("\n");

                    // Neue Liste erzeugen
                    java.util.List<String> entries = new java.util.ArrayList<>();

                    for (String line : lines) {

                        // Leerzeichen am Anfang und Ende entfernen,
                        // damit jede Zeile sauber gespeichert wird.
                        String cleanedLine = line.trim();

                        // Leere Zeilen ignorieren.
                        if (!cleanedLine.isEmpty()) {
                            entries.add(cleanedLine);
                        }

                    }

                    // Die GUI ersetzt die Einträge nicht direkt,
                    // sondern gibt die neue Textliste an den Controller weiter.
                    // Gespeichert wird später gesammelt, z. B. beim Beenden der App.
                    controller.replaceTextEntries(textList, entries);
                }

                @Override
                public void insertUpdate(DocumentEvent e) {
                    updateEntries();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    updateEntries();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    updateEntries();
                }
            });

            JScrollPane textScrollPane = new JScrollPane(textArea);

            textScrollPane.setPreferredSize(new Dimension(400, 400));

            todoPanel.setLayout(new BorderLayout());
            todoPanel.add(textScrollPane, BorderLayout.CENTER);

            // Cursor direkt ins Textfeld setzen,
            // sobald Swing die Ansicht fertig aktualisiert hat.
            SwingUtilities.invokeLater(() -> textArea.requestFocusInWindow());
        }

        todoPanel.revalidate();
        todoPanel.repaint();
    }

}