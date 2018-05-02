package edu.utrack.test.goals;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.utrack.goals.GoalManager;

class ArchivedGoalTest1 {
	
	private File file = new File("goals.dat");
	private GoalManager manager;

	@BeforeEach
	void setUp() throws Exception {
		file.delete();
	}

	@Test
	void test() {
		manager = new GoalManager(file);
		//check that there are no archived goals already in the file
		assertEquals(0, manager.getArchivedGoals().size());
	
	}

}
