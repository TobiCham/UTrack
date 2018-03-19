package edu.utrack.settings;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class ConfigSerializer implements JsonSerializer<ConfigSection>, JsonDeserializer<ConfigSection>{

	@Override
	public JsonElement serialize(ConfigSection src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject object = new JsonObject();
		for(Entry<String, Object> entry : src.getMap().entrySet()) {
			object.add(entry.getKey(), context.serialize(entry.getValue()));
		}
		return object;
	}
	
	@Override
	public ConfigSection deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		JsonObject object = (JsonObject) json;
		ConfigSection section = new ConfigSection();
		for(Entry<String, JsonElement> entry : object.entrySet()) {
			section.set(entry.getKey(), getValue(entry.getValue(), context));
		}
		return section;
	}
	
	private Object getValue(JsonElement element, JsonDeserializationContext context) {
		if(element instanceof JsonObject) return create((JsonObject) element, context);
		else if(element instanceof JsonArray) {
			JsonArray arr = (JsonArray) element;
			List<Object> list = new ArrayList<>();
			for(int i = 0; i < arr.size(); i++) list.add(getValue(arr.get(i), context));
			return list;
		}
		else if(element instanceof JsonPrimitive) {
			JsonPrimitive prim = (JsonPrimitive) element;
			if(prim.isBoolean()) return prim.getAsBoolean();
			if(prim.isNumber()) return prim.getAsNumber();
			if(prim.isString()) return prim.getAsString();
		}
		return null;
	}
	
	private ConfigSection create(JsonObject object, JsonDeserializationContext context) {
		Map<String, Object> map = context.deserialize(object, Map.class);
		ConfigSection section = new ConfigSection();
		section.getMap().putAll(map);
		return section;
	}
}
