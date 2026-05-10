package model;

/**
 * Todo-Liste als einfacher Fließtext.
 */
public class TextTodoList extends TodoList {

    // Inhalt der Liste
    private String content;

    /**
     * Konstruktor
     */
    public TextTodoList(String title) {
        super(title);
        this.content = "";
    }

    @Override
    public String getType() {
        return "text";
    }

    /**
     * Gibt den Inhalt zurück
     */
    public String getContent() {
        return content;
    }

    /**
     * Setzt den Inhalt der Liste
     */
    public void setContent(String content) {
        this.content = content;
    }
}