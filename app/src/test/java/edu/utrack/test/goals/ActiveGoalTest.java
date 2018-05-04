package edu.utrack.test.goals;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.utrack.goals.GoalActivityData;
import edu.utrack.goals.GoalManager;
import edu.utrack.goals.ObjectiveType;
import edu.utrack.goals.ObjectiveValueType;
import edu.utrack.goals.active.ActiveObjective;

class ActiveGoalTest {

	private File file = new File("goals.dat");
	private GoalManager manager;
	
	@BeforeEach
	void setUp() throws Exception {
		file.delete();
	}

	@Test
	void test() {
		manager = new GoalManager(file);

		//For each combination of ObjectiveType and ObjectiveValueType, test the following 3 states:
        // - Failure of the objective
        // - No data for the objective
        // - Completion of the objective

		ActiveObjective aObjective1 = new ActiveObjective("objective1", ObjectiveType.APP_TIME, ObjectiveValueType.ABSOLUTE, 5);
		assertEquals(-1, aObjective1.getCompletionState(new GoalActivityData(10, 0, 0, 1)));
        assertEquals(0, aObjective1.getCompletionState(new GoalActivityData(10, 10, 10, 0)));
        assertEquals(1, aObjective1.getCompletionState(new GoalActivityData(5, 10, 10, 1)));

        //Test against 100 seconds, so each second is 1% of the time
		ActiveObjective aObjective2 = new ActiveObjective("objective2", ObjectiveType.APP_TIME, ObjectiveValueType.PERCENTAGE, 5.0);
        assertEquals(-1, aObjective2.getCompletionState(new GoalActivityData(10, 0, 0, 100)));
        assertEquals(0, aObjective2.getCompletionState(new GoalActivityData(10, 10, 10, 0)));
        assertEquals(1, aObjective2.getCompletionState(new GoalActivityData(5, 10, 10, 100)));


		ActiveObjective aObjective3 = new ActiveObjective("objective3", ObjectiveType.SCREEN_ON, ObjectiveValueType.ABSOLUTE, 5);
        assertEquals(-1, aObjective3.getCompletionState(new GoalActivityData(0, 0, 10, 1)));
        assertEquals(0, aObjective3.getCompletionState(new GoalActivityData(10, 10, 10, 0)));
        assertEquals(1, aObjective3.getCompletionState(new GoalActivityData(10, 10, 5, 1)));

        //Test against 1 hour of time
		ActiveObjective aObjective4 = new ActiveObjective("objective4", ObjectiveType.SCREEN_ON, ObjectiveValueType.PER_HOUR, 5.0);
        assertEquals(-1, aObjective4.getCompletionState(new GoalActivityData(0, 0, 10, 3600)));
        assertEquals(0, aObjective4.getCompletionState(new GoalActivityData(10, 10, 10, 0)));
        assertEquals(1, aObjective4.getCompletionState(new GoalActivityData(10, 10, 5, 3600)));
	}

}
