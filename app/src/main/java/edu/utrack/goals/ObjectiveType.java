package edu.utrack.goals;

import java.util.ArrayList;
import java.util.List;

public enum ObjectiveType {

    APP_TIME(0, "App Time", ObjectiveValueType.ABSOLUTE, ObjectiveValueType.PERCENTAGE),
    SCREEN_ON(1, "Screen Ons", ObjectiveValueType.ABSOLUTE, ObjectiveValueType.PER_HOUR);

    private int id;
    private String name;
    private ObjectiveValueType[] valueTypes;
    private List<String> valueTypeNames;

    private static List<String> namesList;

    ObjectiveType(int id, String name, ObjectiveValueType...types) {
        this.id = id;
        this.name = name;
        this.valueTypes = types;
        valueTypeNames = new ArrayList<>(types.length);
        for(ObjectiveValueType type : types) valueTypeNames.add(type.getDescription());
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static List<String> getNamesList() {
        if(namesList == null) {
            namesList = new ArrayList<>(values().length);
            for(ObjectiveType type : values()) namesList.add(type.name);
        }
        return namesList;
    }

    public List<String> getValueTypeNames() {
        return valueTypeNames;
    }

    public ObjectiveValueType[] getValueTypes() {
        return valueTypes;
    }

    public static ObjectiveType fromId(int id) {
        for(ObjectiveType type : values()) {
            if(type.id == id) return type;
        }
        return null;
    }
}
