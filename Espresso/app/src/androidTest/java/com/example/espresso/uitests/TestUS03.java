package com.example.espresso.uitests;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;


import static org.hamcrest.Matchers.anything;

import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.test.espresso.contrib.PickerActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.GrantPermissionRule;


import com.example.espresso.MainActivity;
import com.example.espresso.R;

import org.hamcrest.Matchers;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@LargeTest
public class TestUS03 {
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    @Rule
    public GrantPermissionRule mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.POST_NOTIFICATIONS");

    /**
     * Test for US 03.01.01, US 03.03.02
     * As an admin, I want to be able to remove events
     * As an admin, I want to be able to remove hashed QR code data
     */
    @Test
    public void testRemoveEvent() throws InterruptedException {
        // Remove an event
        onView(ViewMatchers.withId(R.id.OrganizerSignInButton)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.nav_facilities_organizer)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.add_facility)).perform(click());
        Thread.sleep(2000);
        onView(withHint("Enter facility name")).perform(replaceText("Test Facility"));
        onView(withId(android.R.id.button1)).perform(click());
        Thread.sleep(2000);

        onView(withId(R.id.nav_home_organizer)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.add_event_button)).perform(click());
        onView(withId(R.id.event_name)).perform(replaceText("Test Events"));
        onView(withId(R.id.location)).perform(click());
        onView(withText("Test Facility")).perform(click());

        onView(withId(R.id.choose_date)).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(2024, 5, 10));
        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.choose_time)).perform(click());
        onView(withClassName(Matchers.equalTo(TimePicker.class.getName()))).perform(PickerActions.setTime(9, 0));
        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.choose_time)).perform(replaceText("12:00"));
        onView(withId(R.id.waiting_list_capacity)).perform(replaceText("50"));
        onView(withId(R.id.registration_until)).perform(replaceText("2024-12-31"));
        onView(withId(R.id.attendee_sample_num)).perform(replaceText("50"));
        onView(withId(R.id.next_button)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.create_event_button)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.nav_profile_organizer)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.sign_out)).perform(click());
        Thread.sleep(2000);

        onView(ViewMatchers.withId(R.id.AdminSignInButton)).perform(click());
        Thread.sleep(2000);
        onView(withText("Test Events")).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.remove_qr_button)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.remove_button)).perform(click());
    }

    /**
     * Test for US 03.02.02
     * As an admin, I want to be able to remove profiles
     */
    @Test
    public void testRemoveProfile() throws InterruptedException {
        // Remove a profile
        onView(withId(R.id.AdminSignInButton)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.users)).perform(click());
        Thread.sleep(2000);
        onData(anything())
                .inAdapterView(withId(R.id.user_list_view))
                .atPosition(0)
                .perform(click());

    }

    /**
     * Test for US 03.04.01, US 03.05.01, US 03.06.01
     * As an admin, I want to be able to browse events
     * As an admin, I want to be able to browse profiles
     * As an admin, I want to be able to browse images
     */
    @Test
    public void testBrowseEventsProfilesImages() throws InterruptedException {
        // Browse events
        onView(ViewMatchers.withId(R.id.OrganizerSignInButton)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.nav_profile_organizer)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.sign_out)).perform(click());
        Thread.sleep(2000);

        onView(ViewMatchers.withId(R.id.AdminSignInButton)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.users)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.facilities)).perform(click());
        Thread.sleep(2000);
    }

    /**
     * Test for US 03.07.01
     * As an admin, I want to be able to remove facilities
     */
    @Test
    public void testRemoveFacility() throws InterruptedException {
        // Remove a facility
        onView(ViewMatchers.withId(R.id.OrganizerSignInButton)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.nav_facilities_organizer)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.add_facility)).perform(click());
        Thread.sleep(2000);
        onView(withHint("Enter facility name")).perform(replaceText("Test Facility"));
        onView(withId(android.R.id.button1)).perform(click());
        Thread.sleep(2000);

        onView(withId(R.id.nav_profile_organizer)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.sign_out)).perform(click());
        Thread.sleep(2000);

        onView(ViewMatchers.withId(R.id.AdminSignInButton)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.facilities)).perform(click());
        Thread.sleep(2000);
        onView(withText("Test Facility")).perform(click());
        Thread.sleep(2000);
        onView(withText("OK")).perform(click());
    }


}
