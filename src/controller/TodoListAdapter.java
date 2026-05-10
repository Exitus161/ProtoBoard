package controller;

import com.google.gson.*;
import model.CheckboxTodoList;
import model.TextTodoList;
import model.TodoList;

import java.lang.reflect.Type;

/**
 * Gson Adapter für polymorphe TodoListen.
 * Entscheidet anhand des Typs,
 * welche Unterklasse geladen werden muss.
 */
public class TodoListAdapter implements JsonDeserializer<TodoList> {

    /**
     * Deserialisiert eine TodoList anhand des type-Feldes.
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