package edu.utrack.goals.archived;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.utrack.goals.Goal;
import edu.utrack.goals.GoalActivityData;
import edu.utrack.goals.GoalType;

public class ArchivedGoal extends Goal<ArchivedObjective> {

    private final long beginTime, endTime;
    private final GoalActivityData activityData;
    private final int completionState;
    private List<ArchivedObjective> objectives = new ArrayList<>();

    public ArchivedGoal(GoalType type, List<ArchivedObjective> objectives, long beginTime, long endTime, GoalActivityData activityData) {
        super(type);
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.objectives.addAll(objectives);
        this.objectives = Collections.unmodifiableList(objectives);
        this.completionState = calculateCompletionState(objectives);
        this.activityData = activityData;
    }

    @Override
    public List<ArchivedObjective> getObjectives() {
        return objectives;
    }

    public long getBeginTime() {
        return beginTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public GoalActivityData getActivityData() {
        return activityData;
    }

    /**
     * -1 indicates the goal has been failed. Occurs if any of the objectives are failed<br />
     * 0 indicates there isn't any data
     * 1 indicates the goal has succeeded. Occurs if not failed, and at least 1 of the objectives has been passed
     */
    public int getCompletionState() {
        return completionState;
    }

    private static int calculateCompletionState(List<ArchivedObjective> objectives) {
        if(objectives.isEmpty()) return 0;

        for(ArchivedObjective objective : objectives) {
            if(objective.getCompletedState() < 0) return -1;
        }
        for(ArchivedObjective objective : objectives) {
            if(objective.getCompletedState() > 0) return 1;
        }
        return 0;
    }

    @Override
    protected String[] getFieldNames() {
        return combineNames(super.getFieldNames(), "begin", "end", "activityData");
    }

    @Override
    protected Object[] getFields() {
        return combineFields(super.getFields(), beginTime, endTime, activityData);
    }
}
