package view;

import controller.TodoController;
import model.TextTodoList;
import model.CheckboxTodoList;
import model.TodoApp;
import model.TodoItem;
import model.TodoList;

import javax.swing.*;
import java.awt.*;

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

        todoPanel = new JPanel();
        todoPanel.setLayout(new BoxLayout(todoPanel, BoxLayout.Y_AXIS));

        JScrollPane rightScrollPane = new JScrollPane(todoPanel);

        JButton addItemButton = new JButton("Add Todo");

        addItemButton.addActionListener(e -> addNewItem());

        JPanel rightPanel = new JPanel(new BorderLayout());
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

        if (currentList == null) {

            todoPanel.revalidate();
            todoPanel.repaint();
            return;
        }

        // -----------------------------------
        // Checkbox-Liste anzeigen
        // -----------------------------------

        if (currentList instanceof CheckboxTodoList checkboxList) {

            for (TodoItem item : checkboxList.getItems()) {

                JCheckBox checkBox = new JCheckBox(item.getText());

                checkBox.setSelected(item.isCompleted());

                // Checkbox Event
                checkBox.addActionListener(e -> {

                    controller.toggleItem(checkboxList, item);

                    refreshTodoPanel();
                });

                todoPanel.add(checkBox);
            }
        }

        // -----------------------------------
        // Text-Liste anzeigen
        // -----------------------------------

        else if (currentList instanceof TextTodoList textList) {

            for (String entry : textList.getEntries()) {

                JLabel label = new JLabel(entry);

                todoPanel.add(label);
            }
        }

        todoPanel.revalidate();
        todoPanel.repaint();
    }

}