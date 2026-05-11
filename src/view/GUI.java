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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


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

        createFrame();

        JPanel leftPanel = createLeftPanel();
        JPanel rightPanel = createRightPanel();

        // LISTENAUSWAHL

        registerListSelectionListener();

        // FRAME ZUSAMMENSETZEN

        frame.add(leftPanel, BorderLayout.WEST);
        frame.add(rightPanel, BorderLayout.CENTER);

        // Linke Listenübersicht aktualisieren.
        refreshListOverview();

        // Passenden Startzustand anzeigen.
        selectFirstListIfAvailable();

        // Fenster mittig auf dem Bildschirm platzieren.
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     * Erstellt und konfiguriert das Hauptfenster.
     */
    private void createFrame() {

        frame = new JFrame("ProtoBoard");
        frame.setSize(700, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Beim Schließen des Fensters den aktuellen Stand speichern.
        frame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {

                // Aktuelle Daten vor dem Beenden speichern.
                controller.save();
            }
        });
    }

    /**
     * Erstellt die linke Seitenleiste mit Listenübersicht und Erstellen-Button.
     */
    private JPanel createLeftPanel() {

        listModel = new DefaultListModel<>();
        listOverview = new JList<>(listModel);

        // Rechtsklick-Menü für Listen erstellen.
        JPopupMenu popupMenu = createListPopupMenu();

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

        // Rechtsklick-Menü aktivieren.
        listOverview.setComponentPopupMenu(popupMenu);

        JScrollPane leftScrollPane = new JScrollPane(listOverview);

        JButton addListButton = new JButton("Create List");

        addListButton.addActionListener(e -> createNewList());

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(leftScrollPane, BorderLayout.CENTER);
        leftPanel.add(addListButton, BorderLayout.SOUTH);

        leftPanel.setPreferredSize(new Dimension(200, 500));

        return leftPanel;
    }

    /**
     * Erstellt die rechte Hauptansicht mit Titel, Inhalt und Add-Button.
     */
    private JPanel createRightPanel() {

        // Panel für die Todo-Inhalte erstellen.
        todoPanel = new JPanel();
        todoPanel.setLayout(new BoxLayout(todoPanel, BoxLayout.Y_AXIS));

        JScrollPane rightScrollPane = new JScrollPane(todoPanel);

        // Titel der aktuell ausgewählten Liste.
        currentListLabel = new JLabel("No list selected");
        currentListLabel.setFont(new Font("Arial", Font.BOLD, 20));

        // Button zum Hinzufügen neuer Aufgaben.
        addItemButton = new JButton("Add Task");
        addItemButton.addActionListener(e -> addNewItem());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(currentListLabel, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new BorderLayout());

        rightPanel.add(topPanel, BorderLayout.NORTH);
        rightPanel.add(rightScrollPane, BorderLayout.CENTER);
        rightPanel.add(addItemButton, BorderLayout.SOUTH);

        return rightPanel;
    }

    /**
     * Registriert die Reaktion auf die Auswahl einer Liste.
     */
    private void registerListSelectionListener() {

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
    }

    /**
     * Wählt beim Start automatisch die erste Liste aus,
     * falls gespeicherte Listen vorhanden sind.
     */
    private void selectFirstListIfAvailable() {

        if (!app.getLists().isEmpty()) {

            // Erste vorhandene Liste auswählen.
            // Dadurch wird auch die rechte Ansicht aktualisiert.
            listOverview.setSelectedIndex(0);

        } else {

            // Wenn keine Listen vorhanden sind,
            // wird der leere Auswahlzustand angezeigt.
            refreshTodoPanel();
        }
    }

    /**
     * Erstellt das Rechtsklick-Menü für die Listenübersicht.
     */
    private JPopupMenu createListPopupMenu() {

        JPopupMenu popupMenu = new JPopupMenu();

        JMenuItem deleteItem = new JMenuItem("Delete List");
        JMenuItem renameItem = new JMenuItem("Rename List");

        deleteItem.addActionListener(e -> deleteSelectedList());
        renameItem.addActionListener(e -> renameSelectedList());

        popupMenu.add(deleteItem);
        popupMenu.add(renameItem);

        return popupMenu;
    }

    /**
     * Löscht die aktuell ausgewählte Liste nach Bestätigung.
     */
    private void deleteSelectedList() {

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

            // Nach dem Löschen eine passende verbleibende Liste auswählen.
            selectListAfterDelete(selectedIndex);
        }
    }

    /**
     * Benennt die aktuell ausgewählte Liste um.
     */
    private void renameSelectedList() {

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
    }

    /**
     * Wählt nach dem Löschen einer Liste eine passende verbleibende Liste aus.
     */
    private void selectListAfterDelete(int deletedIndex) {

        if (!app.getLists().isEmpty()) {

            // Wenn die letzte Liste gelöscht wurde,
            // nehmen wir den neuen letzten Index.
            int nextIndex = Math.min(deletedIndex, app.getLists().size() - 1);

            // Die nächste Liste wird links ausgewählt.
            // Dadurch wird auch der ListSelectionListener ausgelöst.
            listOverview.setSelectedIndex(nextIndex);

        } else {

            // Wenn keine Liste mehr vorhanden ist,
            // gibt es auch keine aktuelle Liste mehr.
            currentList = null;

            // Rechte Ansicht auf "No list selected" zurücksetzen.
            refreshTodoPanel();
        }
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
     * Aktualisiert die rechte Todo-Ansicht.
     */
    private void refreshTodoPanel() {

        todoPanel.removeAll();

        // Keine Liste ausgewählt.
        if (currentList == null) {

            showNoListSelected();

        } else {

            // Titel der aktuellen Liste anzeigen.
            currentListLabel.setText(currentList.getTitle());

            if (currentList instanceof CheckboxTodoList checkboxList) {

                showCheckboxList(checkboxList);

            } else if (currentList instanceof TextTodoList textList) {

                showTextList(textList);
            }
        }

        todoPanel.revalidate();
        todoPanel.repaint();
    }

    /**
     * Zeigt den Zustand an, wenn keine Liste ausgewählt ist.
     */
    private void showNoListSelected() {

        // Text für den leeren Auswahlzustand setzen.
        currentListLabel.setText("No list selected");

        // Ohne ausgewählte Liste kann kein neuer Eintrag hinzugefügt werden.
        addItemButton.setEnabled(false);
    }

    /**
     * Zeigt eine Checkbox-Liste mit allen Aufgaben an.
     */
    private void showCheckboxList(CheckboxTodoList checkboxList) {

        // Bei Checkbox-Listen werden neue Einträge über den Button hinzugefügt.
        addItemButton.setEnabled(true);

        // Layout für Checkbox-Listen setzen.
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

            // Erledigte Tasks hervorheben.
            if (item.isCompleted()) {

                checkBox.setForeground(Color.GRAY);

                Font oldFont = checkBox.getFont();

                checkBox.setFont(
                        oldFont.deriveFont(
                                oldFont.getStyle() | Font.ITALIC
                        )
                );
            }

            // Rechtsklick-Menü für die Aufgabe erstellen.
            JPopupMenu popupMenu = new JPopupMenu();

            JMenuItem editItem = new JMenuItem("Edit");
            JMenuItem deleteItem = new JMenuItem("Delete");

            popupMenu.add(editItem);
            popupMenu.add(deleteItem);

            checkBox.setComponentPopupMenu(popupMenu);

            // Aufgabe bearbeiten.
            editItem.addActionListener(e -> editCheckboxItem(item));

            // Aufgabe löschen.
            deleteItem.addActionListener(e -> deleteCheckboxItem(checkboxList, item));

            // Checkbox-Status ändern.
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

    /**
     * Öffnet den Dialog zum Bearbeiten einer Checkbox-Aufgabe.
     */
    private void editCheckboxItem(TodoItem item) {

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

            // Die Änderung wird über den Controller ausgeführt.
            controller.editItem(item, newText);

            refreshTodoPanel();
        }
    }

    /**
     * Fragt nach Bestätigung und löscht danach eine Checkbox-Aufgabe.
     */
    private void deleteCheckboxItem(CheckboxTodoList checkboxList, TodoItem item) {

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

        // Die Aufgabe wird über den Controller gelöscht.
        controller.removeItem(checkboxList, item);

        refreshTodoPanel();
    }

    /**
     * Zeigt eine Textliste als frei bearbeitbares Textfeld an.
     */
    private void showTextList(TextTodoList textList) {

        // Textlisten werden direkt im Textfeld bearbeitet.
        addItemButton.setEnabled(false);

        textArea = new JTextArea();

        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        // Hinweis anzeigen, wenn der Benutzer mit der Maus über dem Textfeld bleibt.
        textArea.setToolTipText("Write one note per line");

        // Vorhandene Einträge anzeigen.
        StringBuilder builder = new StringBuilder();

        for (String entry : textList.getEntries()) {
            builder.append(entry).append("\n");
        }

        textArea.setText(builder.toString());

        // Änderungen im Textfeld überwachen.
        textArea.getDocument().addDocumentListener(new DocumentListener() {

            /**
             * Aktualisiert die Einträge im Model.
             */
            private void updateEntries() {

                // Gesamten Text holen.
                String text = textArea.getText();

                // Nach Zeilen trennen.
                String[] lines = text.split("\n");

                // Neue Liste erzeugen.
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
}