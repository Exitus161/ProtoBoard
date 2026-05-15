package controller;

import com.google.gson.*;
import model.CheckboxTodoList;
import model.TextTodoList;
import model.TodoList;

import java.lang.reflect.Type;

/**
 * Gson Adapter für polymorphe TodoListen.
 * 
 * Diese Klasse implementiert {@link JsonDeserializer<TodoList>} und entscheidet
 * beim Laden aus JSON anhand des Feldes {@code type}, welche konkrete
 * {@link model.TodoList}-Unterklasse erzeugt werden soll.
 * 
 * Das erlaubt es, in der Persistenzschicht verschiedene Listentypen
 * (@link model.CheckboxTodoList} und {@link model.TextTodoList}) polymorph zu
 * handhaben, ohne dass der Verbrauchscode die konkrete Implementierung kennen muss.
 */
public class TodoListAdapter implements JsonDeserializer<TodoList> {

    /**
     * Deserialisiert eine TodoList anhand des {@code type}-Feldes.
     * 
     * @param json Das JSON-Element, das die TodoList repräsentiert
     * @param typeOfT Der erwartete Typ ({@code TodoList.class})
     * @param context Der Deserialisierungs-Kontext von Gson
     * @return Eine konkrete {@link model.TodoList}-Instanz
     * @throws JsonParseException Wenn das {@code type}-Feld fehlt oder ein unbekannter Typ angegeben ist
     */
    @Override
    public TodoList deserialize(JsonElement json,
                                Type typeOfT,
                                JsonDeserializationContext context)
            throws JsonParseException {

        JsonObject jsonObject = json.getAsJsonObject();

        // Typ aus JSON lesen
        JsonElement typeElement = jsonObject.get("type");

        if (typeElement == null) {
            throw new JsonParseException("Kein type-Feld gefunden.");
        }

        // Das type-Feld entscheidet, welche konkrete Listenklasse erzeugt wird.
        String type = typeElement.getAsString();

        // Checkbox-Liste
        if (type.equals("checkbox")) {

            return context.deserialize(json, CheckboxTodoList.class);
        }

        // Text-Liste
        if (type.equals("text")) {

            return context.deserialize(json, TextTodoList.class);
        }

        throw new JsonParseException("Unbekannter Listentyp: " + type);
    }
}