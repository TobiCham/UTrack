package edu.utrack.goals;

import edu.utrack.util.DataClass;

public abstract class Objective extends DataClass {

    private String name;
    private ObjectiveType type;
    private ObjectiveValueType valueType;
    protected Number value;

    public Objective(String name, ObjectiveType type, ObjectiveValueType valueType, Number value) {
        this.name = name;
        this.type = type;
        this.valueType = valueType;
        this.value = value;
    }

    public String getDescription() {
        return name;
    }

    public ObjectiveType getType() {
        return type;
    }

    public ObjectiveValueType getValueType() {
        return valueType;
    }

    public Number getValue() {
        return value;
    }

    @Override
    protected String[] getFieldNames() {
        return new String[] {"name", "type", "valueType", "value"};
    }

    @Override
    protected Object[] getFields() {
        return new Object[] {name, type, valueType, value};
    }
}
