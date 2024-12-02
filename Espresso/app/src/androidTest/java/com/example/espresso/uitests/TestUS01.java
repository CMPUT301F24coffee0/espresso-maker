package com.example.espresso.uitests;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressBack;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;


import static org.hamcrest.Matchers.anything;

import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.test.espresso.contrib.PickerActions;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.GrantPermissionRule;


import com.example.espresso.MainActivity;
import com.example.espresso.Organizer.WaitingList;
import com.example.espresso.R;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.util.UUID;


@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@LargeTest
public class TestUS01 {
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    @Rule
    public GrantPermissionRule mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.POST_NOTIFICATIONS");

    @Rule
    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule
            .grant(android.Manifest.permission.ACCESS_FINE_LOCATION);

    @Before
    public void createEvent() throws InterruptedException {
        onView(ViewMatchers.withId(R.id.OrganizerSignInButton)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.nav_facilities_organizer)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.add_facility)).perform(click());
        Thread.sleep(3000);
        onView(withHint("Enter facility name")).perform(replaceText("Test Facility"));
        onView(withId(android.R.id.button1)).perform(click());
        Thread.sleep(3000);

        onView(withId(R.id.nav_home_organizer)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.add_event_button)).perform(click());
        onView(withId(R.id.event_name)).perform(replaceText("Test Event"));
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
        Thread.sleep(3000);
        onView(withId(R.id.create_event_button)).perform(click());
        Thread.sleep(4000);
    }

    @After
    public void deleteEvent() throws InterruptedException {
        onView(ViewMatchers.withId(R.id.AdminSignInButton)).perform(click());
        Thread.sleep(3000);
        onView(withText("Test Event")).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.remove_button)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.users)).perform(click());
        Thread.sleep(3000);
        onData(anything())
                .inAdapterView(withId(R.id.user_list_view))
                .atPosition(0)
                .perform(click());
        Thread.sleep(3000);
        onView(withText("Yes")).perform(click());
    }

    /**
     * Tests for US 01.01.01, US 01.01.02, US 01.07.01
     * As an entrant, I want to join the waiting list for a specific event.
     * As an entrant, I want to leave the waiting list for a specific event.
     * As an entrant, I want to be identified by my device, so that I don't have to use a username and password.
     */
    @Test
    public void testJoinLeaveWaitingList() throws InterruptedException {
        // join waiting list
        onView(withId(R.id.nav_profile_organizer)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.sign_out)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.AttendeeSignInButton)).perform(click());
        Thread.sleep(3000);

        onView(withText("Test Event")).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.enter_lottery_button)).perform(click());
        Thread.sleep(3000);
        onView(withText("Yes")).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.events)).perform(click());
        Thread.sleep(3000);
        onView(withText("Pending Events")).perform(click());
        Thread.sleep(3000);
        onView(withText("Test Event")).perform(click());
        Thread.sleep(3000);
        onView(withText("Withdraw from the lottery")).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.profile)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.button)).perform(click());
        Thread.sleep(3000);
    }

    /**
     * Tests for US 01.02.01, US 01.03.03, US 01.03.02
     * As an entrant, I want to provide my personal information such as name, email, and optional phone number in the app.
     * As an entrant, I want my profile picture to be deterministically generated from my profile name if I haven't uploaded a profile image yet.
     * As an entrant, I want to remove profile picture if need be.
     */
    @Test
    public void testProvidePersonalInformation() throws InterruptedException {
        onView(withId(R.id.nav_profile_organizer)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.sign_out)).perform(click());
        Thread.sleep(3000);

        onView(withId(R.id.AttendeeSignInButton)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.profile)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.edit_profile_button)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.edit_name)).perform(replaceText("Test Name"));
        onView(withId(R.id.edit_email)).perform(replaceText("TestEmail@email.com"));
        onView(withId(R.id.edit_phone_number)).perform(replaceText("1234567890"));
        onView(withId(android.R.id.button1)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.removeProfilePicButton)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.button)).perform(click());
        Thread.sleep(3000);
    }

    /**
     * test for US 01.04.03
     * As an entrant, I want to opt out of receiving notifications from organizers and admin.
     */
    @Test
    public void testOptOutOfNotifications() throws InterruptedException {
        onView(withId(R.id.nav_profile_organizer)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.sign_out)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.AttendeeSignInButton)).perform(click());
        Thread.sleep(3000);

        onView(withText("Test Event")).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.enter_lottery_button)).perform(click());
        Thread.sleep(3000);
        onView(withText("No")).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.events)).perform(click());
        Thread.sleep(3000);
        onView(withText("Pending Events")).perform(click());
        Thread.sleep(3000);
        onView(withText("Test Event")).perform(click());
        Thread.sleep(3000);
        onView(withText("Withdraw from the lottery")).perform(click());
        Thread.sleep(3000);

        onView(withId(R.id.profile)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.button)).perform(click());
        Thread.sleep(3000);
    }

    /**
     * test for US 01.08.01
     * As an entrant, I want to be warned before joining a waiting list that requires geolocation.
     */
    @Test
    public void testWarningForGeolocationRequiredWaitingList() throws InterruptedException {
        // Placeholder for future implementation
        onView(withText("Test Event")).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.edit_button)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.next_button)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.geolocation_switch)).perform(click());
        onView(withId(R.id.create_event_button)).perform(click());
        Thread.sleep(3000);

        onView(withId(R.id.nav_profile_organizer)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.sign_out)).perform(click());
        Thread.sleep(3000);

        onView(withId(R.id.AttendeeSignInButton)).perform(click());
        Thread.sleep(3000);

        onView(withText("Test Event")).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.enter_lottery_button)).perform(click());
        Thread.sleep(3000);

        onView(withText("Yes")).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.events)).perform(click());
        Thread.sleep(3000);
        onView(withText("Pending Events")).perform(click());
        Thread.sleep(3000);
        onView(withText("Test Event")).perform(click());
        Thread.sleep(3000);
        onView(withText("Withdraw from the lottery")).perform(click());
        Thread.sleep(3000);

        onView(withId(R.id.profile)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.button)).perform(click());
        Thread.sleep(3000);
    }

}

