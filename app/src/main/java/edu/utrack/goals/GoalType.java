package edu.utrack.goals;

public enum GoalType {

    DAILY(0, "Daily"), WEEKLY(1,  "Weekly");

    private String name;
    private int id;

    GoalType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static GoalType fromId(int id) {
        for(GoalType type : values()) {
            if(type.id == id) return type;
        }
        return null;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
