package com.example.espresso.uitests;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressBack;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
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
import com.example.espresso.R;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;


@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@LargeTest
public class TestUS02 {
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
        Thread.sleep(3000);
        onView(withId(R.id.create_event_button)).perform(click());
        Thread.sleep(4000);
    }

    @After
    public void deleteEvent() throws InterruptedException {
        onView(ViewMatchers.withId(R.id.AdminSignInButton)).perform(click());
        Thread.sleep(3000);
        onView(withText("Test Events")).perform(click());
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
     * Test for US 02.01.01
     * As an organizer, I want to be able to create and manage events & generate a promo QR code
     */
    @Test
    public void testCreateEventAndGenerateQRCode() throws InterruptedException {
        // Create a new event
        onView(withId(R.id.nav_profile_organizer)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.sign_out)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.AttendeeSignInButton)).perform(click());
        Thread.sleep(3000);

        onView(withText("Test Events")).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.share_button)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.qr_code_image)).check(matches(isDisplayed()));
        onView(withId(R.id.cancel_button)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.go_back_button)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.profile)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.button)).perform(click());
        Thread.sleep(3000);


    }

    /**
     * Test for US 02.01.03
     * As an organizer, I want to be able to create and manage my facility profile
     */
    @Test
    public void testCreateFacilityProfile() throws InterruptedException {
        // Create a new facility profile
        onView(withId(R.id.nav_facilities_organizer)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.add_facility)).perform(click());
        Thread.sleep(3000);
        onView(withHint("Enter facility name")).perform(replaceText("Test Facility"));
        onView(withId(android.R.id.button1)).perform(click());
        Thread.sleep(3000);
        onView(withText("Test Facility")).check(matches(isDisplayed()));
        onView(withText("Test Facility")).perform(click());
        Thread.sleep(3000);
        onView(withText("Delete Facility")).perform(click());
        Thread.sleep(3000);

        onView(withId(R.id.nav_profile_organizer)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.sign_out)).perform(click());
        Thread.sleep(3000);


    }

    /**
     * US 02.02.01, 02.06.01, 02.06.02, 02.06.03
     * As an organizer, I want to be able to view the list of entrants on the waiting list
     * As an organizer I want to view a list of all chosen entrants who are invited to apply
     * As an organizer I want to see a list of all the cancelled entrants
     * As an organizer, I want to be able to view the final list of entrants who enrolled for the event
     */
    @Test
    public void testViewWaitingList() throws InterruptedException {
        // View the waiting list
        onView(withText("Test Events")).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.entrant_button)).perform(click());
        Thread.sleep(3000);
        onView(withText("Invited")).perform(click());
        Thread.sleep(3000);
        onView(withText("Declined")).perform(click());
        Thread.sleep(3000);
        onView(withText("All")).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.closeButton)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.go_back_button)).perform(click());
        Thread.sleep(3000);

        onView(withId(R.id.nav_profile_organizer)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.sign_out)).perform(click());
        Thread.sleep(3000);


    }

    /**
     * Test for US 02.02.02
     * As an organizer I want to see on a map where entrants joined my event waiting list from.
     */
    @Test
    public void testMapEntrants() throws InterruptedException {
        // Map entrants on the waiting list
        onView(withText("Test Events")).perform(click());
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

        onView(withText("Test Events")).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.enter_lottery_button)).perform(click());
        Thread.sleep(3000);
        onView(withText("Yes")).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.profile)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.button)).perform(click());
        Thread.sleep(3000);

        onView(withId(R.id.OrganizerSignInButton)).perform(click());
        Thread.sleep(3000);
        onView(withText("Test Events")).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.map_button)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.map_go_back_button)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.go_back_button)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.nav_profile_organizer)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.sign_out)).perform(click());
        Thread.sleep(3000);

        onView(withId(R.id.AttendeeSignInButton)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.events)).perform(click());
        Thread.sleep(3000);
        onView(withText("Pending Events")).perform(click());
        Thread.sleep(3000);
        onView(withText("Test Events")).perform(click());
        Thread.sleep(3000);
        onView(withText("Withdraw from the lottery")).perform(click());
        Thread.sleep(3000);

        onView(withId(R.id.profile)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.button)).perform(click());
        Thread.sleep(3000);

    }

    /**
     * Test for US 02.04.02
     * As an organizer, I want to be able to update an event poster
     */
    @Test
    public void testUpdatePoster() throws InterruptedException {
        // Update an event poster
        onView(withText("Test Events")).perform(click());
        Thread.sleep(6000);
        onView(withId(R.id.edit_button)).perform(click());
        Thread.sleep(6000);
        onView(withId(R.id.location)).perform(click());
        onView(withText("Test Facility")).perform(click());

        onView(withId(R.id.next_button)).perform(click());
        Thread.sleep(6000);
        onView(withId(R.id.upload_poster_button)).check(matches(isDisplayed()));
        onView(withId(R.id.exit_form_button)).perform(click());
        Thread.sleep(6000);
        onView(withId(R.id.go_back_button)).perform(click());
        Thread.sleep(6000);

        onView(withId(R.id.nav_profile_organizer)).perform(click());
        Thread.sleep(6000);
        onView(withId(R.id.sign_out)).perform(click());
        Thread.sleep(6000);

    }

    /**
     * Test for US 02.05.02
     * As an organizer, I want to be able to perform the "lottery"
     */
    @Test
    public void testLottery() throws InterruptedException {
        // Perform the lottery
        onView(withText("Test Events")).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.draw_lottery)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.go_back_button)).perform(click());
        Thread.sleep(3000);

        onView(withId(R.id.nav_profile_organizer)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.sign_out)).perform(click());
        Thread.sleep(3000);

    }

    /**
     * US 02.07.01 Allow organizers to send notifications to all entrants on the waiting list
     */
    @Test
    public void testSendNotificationsWaitingList() throws InterruptedException {
        // Send notifications to all entrants on the waiting list
        onView(withText("Test Events")).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.notification)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.notification_message_input)).perform(replaceText("test notification"));
        onView(withId(R.id.send_notification_button)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.go_back_button)).perform(click());
        Thread.sleep(3000);

        onView(withId(R.id.nav_profile_organizer)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.sign_out)).perform(click());
        Thread.sleep(3000);
    }

}
