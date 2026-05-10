package view;

import controller.TodoController;
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
    private CheckboxTodoList currentList;

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

                    // Nur CheckboxListen behandeln
                    if (selectedList instanceof CheckboxTodoList) {
                        currentList = (CheckboxTodoList) selectedList;
                        refreshTodoPanel();
                    }
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

        if (title != null && !title.isEmpty()) {

            CheckboxTodoList newList = new CheckboxTodoList(title);

            controller.addList(newList);

            refreshListOverview();
        }
    }

    /**
     * Fügt ein neues TodoItem hinzu
     */
    private void addNewItem() {

        if (currentList == null) {
            return;
        }

        String text = JOptionPane.showInputDialog("Neue Aufgabe:");

        if (text != null && !text.isEmpty()) {

            controller.addItem(currentList, text);

            refreshTodoPanel();
        }
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

        if (currentList != null) {

            for (TodoItem item : currentList.getItems()) {

                JCheckBox checkBox = new JCheckBox(item.getText());

                checkBox.setSelected(item.isCompleted());

                // Checkbox Event
                checkBox.addActionListener(e -> {
                    controller.toggleItem(currentList, item);
                    refreshTodoPanel();
                });

                todoPanel.add(checkBox);
            }
        }

        todoPanel.revalidate();
        todoPanel.repaint();
    }
}