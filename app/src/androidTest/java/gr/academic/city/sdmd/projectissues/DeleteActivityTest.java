//package gr.academic.city.sdmd.projectissues;
//
//import android.support.test.rule.ActivityTestRule;
//import android.support.test.runner.AndroidJUnit4;
//import android.test.suitebuilder.annotation.LargeTest;
//
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//import gr.academic.city.sdmd.projectissues.ui.activity.MainActivity;
//
//import static android.support.test.espresso.Espresso.onView;
//import static android.support.test.espresso.action.ViewActions.click;
//import static android.support.test.espresso.action.ViewActions.swipeDown;
//import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
//import static android.support.test.espresso.assertion.ViewAssertions.matches;
//import static android.support.test.espresso.matcher.ViewMatchers.withId;
//import static android.support.test.espresso.matcher.ViewMatchers.withText;
//import static org.hamcrest.core.AllOf.allOf;
//import static org.hamcrest.core.Is.is;
//
//
//@RunWith(AndroidJUnit4.class)
//@LargeTest
//public class DeleteActivityTest {
//
//    @Rule
//    public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class, true, true);
//
//    @Test
//    public void test() {
//
//        String activityTitle = "For Test Use Only";
//        String clubName = "A/V Club";
//
//        onView((withId(R.id.swipe_refresh))).perform(swipeDown());
//        onView(allOf(withId(R.id.tv_club_name), withText(clubName))).perform(click());
//        onView((withId(R.id.swipe_refresh))).perform(swipeDown());
//        onView(allOf(withId(R.id.tv_club_activity_title), withText(activityTitle))).perform(click());
//        onView(withId(R.id.tv_club_activity_title)).check(matches(is(withText(activityTitle))));
//        onView(withId(R.id.action_delete)).perform(click());
//        onView((withId(R.id.swipe_refresh))).perform(swipeDown());
//        onView(allOf(withId(R.id.tv_club_activity_title), withText(activityTitle))).check(doesNotExist());
//
//    }
//}