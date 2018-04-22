package edu.utrack.goals.active;

import edu.utrack.goals.GoalActivityData;
import edu.utrack.goals.Objective;
import edu.utrack.goals.ObjectiveType;
import edu.utrack.goals.ObjectiveValueType;

public class ActiveObjective extends Objective {

    public ActiveObjective(String name, ObjectiveType type, ObjectiveValueType valueType, Number value) {
        super(name, type, valueType, value);
    }

    /**
     * @return -1 for fail, 0 for not enough data, 1 for succeed
     */
    public int getCompletionState(GoalActivityData data) {
        if(data.getTotalEventTime() <= 0) return 0;

        ObjectiveValueType valType = getValueType();
        if(getType() == ObjectiveType.APP_TIME) {
            if(valType == ObjectiveValueType.ABSOLUTE && data.getTotalAppTime() > getValue().doubleValue()) return -1;
            if(valType == ObjectiveValueType.PERCENTAGE && data.getPercentageAppTime() > getValue().doubleValue()) return -1;
        } else {
            if(valType == ObjectiveValueType.ABSOLUTE && data.getScreenOns() > getValue().doubleValue()) return -1;
            if(valType == ObjectiveValueType.PER_HOUR && data.getScreenOnsPerHour() > getValue().doubleValue()) return -1;
        }
        return 1;
    }

    public void setValue(Number value) {
        if(value.getClass() != getValueType().getDataType()) throw new IllegalArgumentException("Invalid number type");
        this.value = value;
    }
}

