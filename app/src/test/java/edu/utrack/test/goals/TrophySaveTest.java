package edu.utrack.test.goals;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.utrack.goals.GoalManager;

class TrophySaveTest {
	
	private File file = new File("goals.dat");
	private GoalManager manager;

	@BeforeEach
	void setUp() throws Exception {
		file.delete();
	}

	void testTrophy(int num) {
		manager.setTrophies(num);
		manager.save();
		//save file and reload, then check the trophies value is the value set
		manager = new GoalManager(file);
		assertEquals(num, manager.getTrophies());
	}
	
	@Test
	void test() {
		manager = new GoalManager(file);
		//set trophie values
		this.testTrophy(Integer.MIN_VALUE);
		this.testTrophy(Integer.MAX_VALUE);
		this.testTrophy(-1);
		this.testTrophy(0);
		this.testTrophy(1);
		
		
	}

}
