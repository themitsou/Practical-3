package gr.academic.city.sdmd.studentsclubactivities;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.widget.AdapterView;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import gr.academic.city.sdmd.studentsclubactivities.db.ClubManagementContract;
import gr.academic.city.sdmd.studentsclubactivities.domain.Club;
import gr.academic.city.sdmd.studentsclubactivities.ui.activity.MainActivity;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.swipeDown;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.Is.is;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class NewActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class, true, true);

    @Test
    public void test() {

        String activityTitle = "For Test Use Only";
        String activityShortNote = "Do NOT Delete Manually";
        String activityLongNote = "For Test Use Only";
        String clubName = "A/V Club";

        onView((withId(R.id.swipe_refresh))).perform(swipeDown());

        onData(anything())
                .inAdapterView(withId(R.id.list)).atPosition(0)
                .perform(click());

        // onView(allOf(withId(R.id.tv_club_name), withText(clubName))).perform(click());

        onView(withId(R.id.action_add)).perform(click());

        onView(withId(R.id.txt_club_activity_title)).perform(typeText(activityTitle), closeSoftKeyboard());
        onView(withId(R.id.txt_club_activity_short_note)).perform(typeText(activityShortNote), closeSoftKeyboard());
        onView(withId(R.id.txt_club_activity_long_note)).perform(typeText(activityLongNote), closeSoftKeyboard());
        onView(withId(R.id.action_save)).perform(click());
        onView(allOf(withId(R.id.tv_club_activity_title), withText(activityTitle))).perform(click());
        onView(withId(R.id.tv_club_activity_title)).check(matches(is(withText(activityTitle))));

    }
}