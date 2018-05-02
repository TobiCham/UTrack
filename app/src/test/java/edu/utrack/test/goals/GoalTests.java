package edu.utrack.test.goals;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses( {
    ActiveGoalTest.class,
    ArchivedGoalTest1.class,
    ArchivedGoalTest2.class,
    DailyGoalTest.class,
    DailyGoalTest2.class,
    TrophySaveTest.class,
    TrophyTest1.class,
    WeeklyGoalTest.class,
    WeeklyGoalTest2.class
})
public class GoalTests { }
