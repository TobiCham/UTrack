package edu.utrack.settings;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigSection {

	private Map<String, Object> map = new HashMap<>();
	
	public void write(File file) throws FileNotFoundException {
		try(PrintWriter writer = new PrintWriter(file)) {
		    writer.print(gson().toJson(map));
		    writer.flush();
        }
	}
	
	public static ConfigSection read(File file) throws IOException {
		StringBuilder builder = new StringBuilder();
	    try(BufferedReader reader = new BufferedReader(new FileReader(file))) {
            while(true) {
                String line = reader.readLine();
                if(line == null) break;
                builder.append(line).append("\n");
            }
	    }
	    return gson().fromJson(builder.toString().trim(), ConfigSection.class); 
	}
	
	private static Gson gson() {
		return new GsonBuilder().registerTypeAdapter(ConfigSection.class, new ConfigSerializer()).create();
	}
	
	public ConfigSection set(String key, Object value) {
		if(value == this) throw new IllegalAccessError("Can't set the config to the config");
		map.put(key, value);
		return this;
	}

	public ConfigSection getSection(String key) {
		Object value = map.get(key);
		if(value == null || !(value instanceof ConfigSection)) return null;
		return (ConfigSection) value;
	}
	
	public List<ConfigSection> getSectionList(String key) {
		Object value = map.get(key);
		if(value == null) return null;
		return gson().fromJson(gson().toJson(value), new TypeToken<List<ConfigSection>>() {}.getType());
	}
	
	public List<Integer> getIntList(String key) {
		Object value = map.get(key);
		if(value == null) return null;
		return gson().fromJson(gson().toJson(value), new TypeToken<List<Integer>>() {}.getType());
	}
	
	public <T> T getType(String key, Class<T> clas) {
		Object value = map.get(key);
		if(value == null) return null;
		return gson().fromJson(gson().toJson(value), clas);
	}
	
	public <T> T getType(String key, Type type) {
		Object value = map.get(key);
		if(value == null) return null;
		return gson().fromJson(gson().toJson(value), type);
	}

	public int getInteger(String key) {
		Object obj = map.get(key);
		return obj instanceof Number ? ((Number) obj).intValue() : 0;
	}

	public double getDouble(String key) {
		Object obj = map.get(key);
		return obj instanceof Number ? ((Number) obj).doubleValue() : 0;
	}

	public float getFloat(String key) {
		Object obj = map.get(key);
		return obj instanceof Number ? ((Number) obj).floatValue() : 0;
	}

	public byte getByte(String key) {
		Object obj = map.get(key);
		return obj instanceof Number ? ((Number) obj).byteValue() : 0;
	}

	public short getShort(String key) {
		Object obj = map.get(key);
		return obj instanceof Number ? ((Number) obj).shortValue() : 0;
	}

	public long getLong(String key) {
		Object obj = map.get(key);
		return obj instanceof Number ? ((Number) obj).longValue() : 0;
	}

	public boolean getBoolean(String key) {
		Object obj = map.get(key);
		if(obj == null) return false;
		if(obj instanceof Boolean) return (boolean) obj;
		return obj != null && Boolean.parseBoolean(obj.toString());
	}

	public String getString(String key) {
		Object obj = map.get(key);
		return (obj == null) ? null : obj.toString();
	}

	public boolean contains(String key) {
		return map.containsKey(key);
	}
	
	public Map<String, Object> getMap() {
		return map;
	}
	
	@Override
	public String toString() {
		return map.toString();
	}
}
