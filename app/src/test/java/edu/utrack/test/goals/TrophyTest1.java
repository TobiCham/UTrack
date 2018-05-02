package edu.utrack.test.goals;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.utrack.goals.GoalManager;

class TrophyTest1 {

	private File file = new File("goals.dat");
	
	
	@BeforeEach
	void setUp() throws Exception {
		file.delete();
	}
	

	@Test
	void test() {
		GoalManager manager = new GoalManager(file);
		//check that there are no trophies already in the file
		assertEquals(manager.getTrophies(), 0);
	}

}
