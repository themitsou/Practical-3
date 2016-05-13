package gr.academic.city.sdmd.studentsclubactivities;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;


// Runs all unit tests.
@RunWith(Suite.class)
@Suite.SuiteClasses({NewActivityTest.class, ActivitiesDetailsTest.class,DeleteActivityTest.class})
public class TestSuite {
}