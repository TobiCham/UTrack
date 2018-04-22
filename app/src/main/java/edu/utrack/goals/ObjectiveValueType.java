package edu.utrack.goals;

public enum ObjectiveValueType {

    ABSOLUTE(0, "#", "Absolute", int.class),
    PERCENTAGE(1, "%", "Percentage", double.class),
    PER_HOUR(2, "/h", "Per hour", double.class);

    private int id;
    private String symbol, description;
    private Class<? extends Number> dataType;

    ObjectiveValueType(int id, String symbol, String description, Class<? extends Number> dataType) {
        this.id = id;
        this.symbol = symbol;
        this.description = description;
        this.dataType = dataType;
    }

    public int getId() {
        return id;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getDescription() {
        return description;
    }

    public Class<? extends Number> getDataType() {
        return dataType;
    }

    public static ObjectiveValueType fromId(int id) {
        for(ObjectiveValueType type : values()) {
            if(type.id == id) return type;
        }
        return null;
    }
}
