package edu.utrack.goals;

import java.util.List;

import edu.utrack.util.DataClass;

public abstract class Goal<T extends Objective> extends DataClass {

    private GoalType type;

    public Goal(GoalType type) {
        this.type = type;
    }

    public abstract List<T> getObjectives();

    public GoalType getType() {
        return type;
    }

    @Override
    protected Object[] getFields() {
        return new Object[] {getType(), getObjectives()};
    }

    @Override
    protected String[] getFieldNames() {
        return new String[] {"type", "objectives"};
    }
}
