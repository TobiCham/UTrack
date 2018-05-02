package edu.utrack.test.goals;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.utrack.goals.GoalManager;

class GoalTest1 {

	private File file = new File("goals.dat");
	private GoalManager manager;
	
	
	@BeforeEach
	void setUp() throws Exception {
		file.delete();
	}

	@Test
	void test() {
		manager = new GoalManager(file);

		for(int i = -50; i < 50; i++) {
			manager.setTrophies(i);
			
			//check the number of trophies is the same as the number set
			assertEquals(i, manager.getTrophies());
			
			manager.save();
			manager = new GoalManager(file);
			
			//save file, reload and check again
			assertEquals(i, manager.getTrophies());
		}
	}

}
