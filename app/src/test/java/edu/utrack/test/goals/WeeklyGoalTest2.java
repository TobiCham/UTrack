package edu.utrack.test.goals;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.utrack.goals.GoalManager;
import edu.utrack.goals.ObjectiveType;
import edu.utrack.goals.ObjectiveValueType;
import edu.utrack.goals.active.ActiveObjective;

class WeeklyGoalTest2 {

	private File file = new File("goals.dat");
	private GoalManager manager;
	
	@BeforeEach
	void setUp() throws Exception {
		file.delete();
	}

	@Test
	void test() {
		manager = new GoalManager(file);
		//create an objective and add it to a weekly goal
		ActiveObjective objective = new ActiveObjective("objective1", ObjectiveType.APP_TIME, ObjectiveValueType.ABSOLUTE, 2);
		manager.getWeeklyGoal().addObjective(objective);
		//check the goal contains the same objective
		assert(manager.getWeeklyGoal().getObjectives().contains(objective));
		manager.save();
		//save and reload, then perform the same tests
		manager = new GoalManager(file);
		assert(manager.getWeeklyGoal().getObjectives().contains(objective));
		
	}

}
