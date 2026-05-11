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

    private TodoController controller;
    private TodoApp app;

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

                TodoList list = app.getLists().get(selectedIndex);

                controller.removeList(list);

                currentList = null;

                refreshListOverview();
                refreshTodoPanel();

                controller.save();
            }
        });

        renameItem.addActionListener(e -> {

            int selectedIndex = listOverview.getSelectedIndex();

            if (selectedIndex >= 0) {

                TodoList list = app.getLists().get(selectedIndex);

                String newTitle = JOptionPane.showInputDialog(
                        frame,
                        "Neuer Listenname:",
                        list.getTitle()
                );

                if (newTitle != null && !newTitle.isBlank()) {

                    list.setTitle(newTitle);

                    controller.save();

                    refreshListOverview();
                    refreshTodoPanel();
                }
            }
        });

        popupMenu.add(deleteItem);
        popupMenu.add(renameItem);

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
        currentListLabel = new JLabel("Keine Liste ausgewählt");
        currentListLabel.setFont(new Font("Arial", Font.BOLD, 20));

        // Button zum Hinzufügen
        JButton addItemButton = new JButton("Add Entry");

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

        frame.setVisible(true);
    }

    /**
     * Erstellt eine neue Liste
     */
    private void createNewList() {

        String title = JOptionPane.showInputDialog("Name der Liste:");

        if (title == null || title.isEmpty()) {
            return;
        }

        // Auswahl des Listentyps
        String[] options = {"Checkbox List", "Text List"};

        int choice = JOptionPane.showOptionDialog(
                frame,
                "Welchen Listentyp erstellen?",
                "Neue Liste",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        TodoList newList;

        // Checkbox-Liste
        if (choice == 0) {

            newList = new CheckboxTodoList(title);

        } else {

            // Freitext-Liste
            newList = new TextTodoList(title);
        }

        controller.addList(newList);
        controller.save();

        refreshListOverview();
    }

    /**
     * Fügt ein neues TodoItem hinzu
     */
    private void addNewItem() {

        if (currentList == null) {
            return;
        }

        String text = JOptionPane.showInputDialog("Neuer Eintrag:");

        if (text == null || text.isEmpty()) {
            return;
        }

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

            textList.addEntry(text);
        }
        controller.save();

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

            currentListLabel.setText("Keine Liste ausgewählt");

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

            // Layout für Checkbox-Liste zurücksetzen
            todoPanel.removeAll();
            todoPanel.setLayout(new BoxLayout(todoPanel, BoxLayout.Y_AXIS));

            for (TodoItem item : checkboxList.getItems()) {

                JCheckBox checkBox = new JCheckBox(item.getText());

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

                // Checkbox Event
                checkBox.addActionListener(e -> {

                    controller.toggleItem(checkboxList, item);

                    controller.save();

                    refreshTodoPanel();
                });

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

                        // Leere Zeilen ignorieren
                        if (!line.trim().isEmpty()) {
                            entries.add(line);
                        }
                    }

                    // Model aktualisieren
                    textList.setEntries(entries);

                    controller.save();
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
        }

        todoPanel.revalidate();
        todoPanel.repaint();
    }

}