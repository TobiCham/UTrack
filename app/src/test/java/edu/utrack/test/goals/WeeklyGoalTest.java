package edu.utrack.test.goals;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.utrack.goals.GoalManager;
import edu.utrack.goals.GoalType;

class WeeklyGoalTest {

	private File file = new File("goals.dat");
	private GoalManager manager;
	
	@BeforeEach
	void setUp() throws Exception {
		file.delete();
	}

	@Test
	void test() {
		manager = new GoalManager(file);
		
		//make sure there are no unrelated objectives
		assertEquals(0, manager.getWeeklyGoal().getObjectives().size());
		//make sure the goal type is weekly
		assertEquals(GoalType.WEEKLY, manager.getWeeklyGoal().getType());
		
	}

}
