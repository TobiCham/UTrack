package edu.utrack.test.goals;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.utrack.goals.GoalActivityData;
import edu.utrack.goals.GoalManager;
import edu.utrack.goals.GoalType;
import edu.utrack.goals.ObjectiveType;
import edu.utrack.goals.ObjectiveValueType;
import edu.utrack.goals.archived.ArchivedGoal;
import edu.utrack.goals.archived.ArchivedObjective;

class ArchivedGoalTest2 {

	private File file = new File("goals.dat");
	private GoalManager manager;
	
	@BeforeEach
	void setUp() throws Exception {
		file.delete();
	}


	@Test
	void test() {
		manager = new GoalManager(file);
		
		ArchivedObjective aObjective1 = new ArchivedObjective("objective1", ObjectiveType.APP_TIME, ObjectiveValueType.ABSOLUTE, 2, 1);
		ArchivedObjective aObjective2 = new ArchivedObjective("objective2", ObjectiveType.SCREEN_ON, ObjectiveValueType.ABSOLUTE, 3, 0);
		ArchivedObjective aObjective3 = new ArchivedObjective("objective3", ObjectiveType.APP_TIME, ObjectiveValueType.PERCENTAGE, 6.0, -1);
		ArchivedObjective aObjective4 = new ArchivedObjective("objective4", ObjectiveType.SCREEN_ON, ObjectiveValueType.PER_HOUR, 42.0, 1);
		
		GoalActivityData goalActivity1 = new GoalActivityData(10, 2, 25, 10000);
		GoalActivityData goalActivity2 = new GoalActivityData(23, 5, 4, 100);
		
		ArrayList<ArchivedObjective> aGoal1List = new ArrayList<>();
		ArrayList<ArchivedObjective> aGoal2List = new ArrayList<>();
		
		aGoal1List.add(aObjective1);
		aGoal1List.add(aObjective3);
		aGoal2List.add(aObjective2);
		aGoal2List.add(aObjective4);
		
		
		ArchivedGoal aGoal1 = new ArchivedGoal(GoalType.DAILY, aGoal1List, 1234, 1234, goalActivity1);
		ArchivedGoal aGoal2 = new ArchivedGoal(GoalType.WEEKLY, aGoal2List, 22222, 333333, goalActivity2);
		
		manager.addArchivedGoal(aGoal1);
		manager.addArchivedGoal(aGoal2);
		
		//check that there are only 2 archived goals
		assertEquals(2, manager.getArchivedGoals().size());
		//make sure that the goals are in the correct order and exact same as the ones we added
		assert(manager.getArchivedGoals().get(0).equals(aGoal1));
		assert(manager.getArchivedGoals().get(1).equals(aGoal2));
		
		manager.save();
		
		manager = new GoalManager(file);
		
		//save the file, reload and perform the same tests
		assertEquals(2, manager.getArchivedGoals().size());
		assert(manager.getArchivedGoals().get(0).equals(aGoal1));
		assert(manager.getArchivedGoals().get(1).equals(aGoal2));
		
	}

}
