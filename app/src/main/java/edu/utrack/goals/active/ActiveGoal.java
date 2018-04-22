package edu.utrack.goals.active;

import java.util.ArrayList;
import java.util.List;

import edu.utrack.goals.Goal;
import edu.utrack.goals.GoalType;

public class ActiveGoal extends Goal<ActiveObjective> {

    private List<ActiveObjective> objectives = new ArrayList<>();

    public ActiveGoal(GoalType type) {
        super(type);
    }

    public void addObjective(ActiveObjective objective) {
        this.objectives.add(objective);
    }

    @Override
    public List<ActiveObjective> getObjectives() {
        return objectives;
    }
}
