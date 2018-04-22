package edu.utrack.goals.archived;

import edu.utrack.goals.Objective;
import edu.utrack.goals.ObjectiveType;
import edu.utrack.goals.ObjectiveValueType;

public final class ArchivedObjective extends Objective {

    private final int completedState;

    public ArchivedObjective(String name, ObjectiveType type, ObjectiveValueType valueType, Number value, int completedState) {
        super(name, type, valueType, value);
        this.completedState = completedState;
    }

    /**
     * @return 0 if no data, < 0 if fail, > 0 if pass
     */
    public int getCompletedState() {
        return completedState;
    }

    @Override
    protected String[] getFieldNames() {
        return combineNames(super.getFieldNames(), "completed");
    }

    @Override
    protected Object[] getFields() {
        return combineFields(super.getFields(), completedState);
    }
}
