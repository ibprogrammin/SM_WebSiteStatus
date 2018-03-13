package com.seriousmonkey.uptimerobot.JsonClasses;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

/**
 * Created by Daniel Sevitti on 2018-03-07.
 */

public class JsonIntTypeAdapter implements JsonDeserializer<JsonInt>, JsonSerializer<JsonInt> {

    public JsonInt deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        int jsonInt;
        try
        {
            jsonInt = json.getAsInt();
        }
        catch (NumberFormatException e)
        {
            jsonInt = 0;
        }
        return new JsonInt(jsonInt);
    }

    public JsonElement serialize(JsonInt src, Type typeOfSrc, JsonSerializationContext context)
    {
        return new JsonPrimitive(src.getValue());
    }
}