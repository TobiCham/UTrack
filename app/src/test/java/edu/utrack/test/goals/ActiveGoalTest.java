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
		
		//4 fails for each type
		ActiveObjective aObjective1 = new ActiveObjective("objective1", ObjectiveType.APP_TIME, ObjectiveValueType.ABSOLUTE, 2);
		ActiveObjective aObjective2 = new ActiveObjective("objective2", ObjectiveType.APP_TIME, ObjectiveValueType.PERCENTAGE, 3);
		ActiveObjective aObjective3 = new ActiveObjective("objective3", ObjectiveType.SCREEN_ON, ObjectiveValueType.PER_HOUR, 56);
		ActiveObjective aObjective4 = new ActiveObjective("objective4", ObjectiveType.SCREEN_ON, ObjectiveValueType.ABSOLUTE, 5.0);
		//4 unsure for each type
		ActiveObjective aObjective5 = new ActiveObjective("objective5", ObjectiveType.SCREEN_ON, ObjectiveValueType.PER_HOUR, 56);
		ActiveObjective aObjective6 = new ActiveObjective("objective6", ObjectiveType.SCREEN_ON, ObjectiveValueType.ABSOLUTE, 0);
		ActiveObjective aObjective7 = new ActiveObjective("objective7", ObjectiveType.APP_TIME, ObjectiveValueType.ABSOLUTE, 2);
		ActiveObjective aObjective8 = new ActiveObjective("objective8", ObjectiveType.APP_TIME, ObjectiveValueType.PERCENTAGE, 3);
		//4 pass for each type
		ActiveObjective aObjective9 = new ActiveObjective("objective9", ObjectiveType.APP_TIME, ObjectiveValueType.PERCENTAGE, 6.0);
		ActiveObjective aObjective10 = new ActiveObjective("objective10", ObjectiveType.APP_TIME, ObjectiveValueType.ABSOLUTE, 90);
		ActiveObjective aObjective11= new ActiveObjective("objective11", ObjectiveType.SCREEN_ON, ObjectiveValueType.PER_HOUR, 56);
		ActiveObjective aObjective12 = new ActiveObjective("objective12", ObjectiveType.SCREEN_ON, ObjectiveValueType.ABSOLUTE, 70);
		
		//4 unsure
		GoalActivityData goalActivity1 = new GoalActivityData(23, 2, 25, 0);
		GoalActivityData goalActivity2 = new GoalActivityData(4, 5, 4, 0);
		GoalActivityData goalActivity3 = new GoalActivityData(5, 6, 9, 0);
		GoalActivityData goalActivity4 = new GoalActivityData(66, 10, 69, 0);
		
		//4 fail
		GoalActivityData goalActivity5 = new GoalActivityData(1234, 5, 72, 145);
		GoalActivityData goalActivity6 = new GoalActivityData(56, 10, 7, 1250);
		GoalActivityData goalActivity7 = new GoalActivityData(5, 2, 25, 10000);
		GoalActivityData goalActivity8 = new GoalActivityData(23, 5, 4, 100);
		
		//4 pass
		GoalActivityData goalActivity9 = new GoalActivityData(9, 6, 9, 1777777777);
		GoalActivityData goalActivity10 = new GoalActivityData(44, 10, 69, 125);
		GoalActivityData goalActivity11 = new GoalActivityData(1234, 5, 72, 1450000);
		GoalActivityData goalActivity12 = new GoalActivityData(56, 10, 7, 1250);
		
		assertEquals(0, aObjective1.getCompletionState(goalActivity1));
		assertEquals(0, aObjective2.getCompletionState(goalActivity2));
		assertEquals(0, aObjective3.getCompletionState(goalActivity3));
		assertEquals(0, aObjective4.getCompletionState(goalActivity4));
		assertEquals(-1, aObjective5.getCompletionState(goalActivity5));
		assertEquals(-1, aObjective6.getCompletionState(goalActivity6));
		assertEquals(-1, aObjective7.getCompletionState(goalActivity7));
		assertEquals(-1, aObjective8.getCompletionState(goalActivity8));
		assertEquals(1, aObjective9.getCompletionState(goalActivity9));
		assertEquals(1, aObjective10.getCompletionState(goalActivity10));
		assertEquals(1, aObjective11.getCompletionState(goalActivity11));
		assertEquals(1, aObjective12.getCompletionState(goalActivity12));
	}

}
