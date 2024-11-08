package com.example.espresso;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.app.Activity;

import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import androidx.test.runner.lifecycle.Stage;

import com.example.espresso.MainActivity;
import com.example.espresso.R;
import com.example.espresso.uitests.UiTest;

import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.util.Collection;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@LargeTest
public class TestUS02 extends UiTest {
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);


    /**
     * Test for US 02.01.01
     * As an organizer, I want to be able to create and manage events & generate a promo QR code
     */
    @Test
    public void testCreateEventAndGenerateQRCode() throws InterruptedException {
        // Create a new event
        onView(withId(R.id.OrganizerSignInButton)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.add_event_button)).perform(click());
        onView(withId(R.id.event_name)).perform(replaceText("Test Event"));
        onView(withId(R.id.location)).perform(typeText("Test Location"));
        onView(withId(R.id.choose_date)).perform(click());
        //onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(2023, 12, 31));
        //onView(withId(android.R.id.button1)).perform(click());
        onView(withId(R.id.choose_date)).perform(typeText("2023-12-31"));
        onView(withId(R.id.choose_time)).perform(replaceText("12:00"));
        onView(withId(R.id.waiting_list_capacity)).perform(replaceText("50"));
        onView(withId(R.id.registration_until)).perform(replaceText("2023-12-31"));
        onView(withId(R.id.next_button)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.create_event_button)).perform(click());
        Thread.sleep(2000);

        onView(withText("Test Event")).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.share_button)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.qr_code_image)).check(matches(isDisplayed()));
    }

    /**
     * Test for US 02.01.02
     * As an organizer, I want to be able to store hash data of the generated QR in the database
     */
    @Test
    public void testStoreHashData() {
        // Store hash data in the database

    }

    /**
     * Test for US 02.01.03
     * As an organizer, I want to be able to create and manage my facility profile
     */
    @Test
    public void testCreateFacilityProfile() {
        // Create a new facility profile
    }

    /**
     * US 02.02.01
     * As an organizer, I want to be able to view the list of entrants on the waiting list
     */
    @Test
    public void testViewWaitingList() throws InterruptedException {
        // View the waiting list
        onView(withId(R.id.OrganizerSignInButton)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.add_event_button)).perform(click());
        onView(withId(R.id.event_name)).perform(replaceText("Test Event"));
        onView(withId(R.id.location)).perform(typeText("Test Location"));
        onView(withId(R.id.choose_date)).perform(click());
        //onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(2023, 12, 31));
        //onView(withId(android.R.id.button1)).perform(click());
        onView(withId(R.id.choose_date)).perform(typeText("2023-12-31"));
        onView(withId(R.id.choose_time)).perform(replaceText("12:00"));
        onView(withId(R.id.waiting_list_capacity)).perform(replaceText("50"));
        onView(withId(R.id.registration_until)).perform(replaceText("2023-12-31"));
        onView(withId(R.id.next_button)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.create_event_button)).perform(click());
        Thread.sleep(2000);

        onView(withText("Test Event")).perform(click());
        Thread.sleep(2000);
        onView(withText("Test Event")).check(matches(isDisplayed()));
        onView(withText("2023-12-31 - 12:00")).check(matches(isDisplayed()));
        onView(withText("Test Location")).check(matches(isDisplayed()));
    }

    /**
     * Test for US 02.04.01
     * As an organizer, I want to be able to upload an event poster
     */
    @Test
    public void testUploadPoster() throws InterruptedException {
        // Upload an event poster
        onView(withId(R.id.OrganizerSignInButton)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.add_event_button)).perform(click());
        onView(withId(R.id.event_name)).perform(replaceText("Test Event"));
        onView(withId(R.id.location)).perform(typeText("Test Location"));
        onView(withId(R.id.choose_date)).perform(click());
        //onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(2023, 12, 31));
        //onView(withId(android.R.id.button1)).perform(click());
        onView(withId(R.id.choose_date)).perform(typeText("2023-12-31"));
        onView(withId(R.id.choose_time)).perform(replaceText("12:00"));
        onView(withId(R.id.waiting_list_capacity)).perform(replaceText("50"));
        onView(withId(R.id.registration_until)).perform(replaceText("2023-12-31"));
        onView(withId(R.id.next_button)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.upload_poster_button)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.go_back_button)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.create_event_button)).perform(click());
        Thread.sleep(2000);
        onView(withText("Test Event")).check(matches(isDisplayed()));
        onView(withText("2023-12-31 12:00")).check(matches(isDisplayed()));
        onView(withText("Test Location")).check(matches(isDisplayed()));
    }

    /**
     * Test for US 02.04.02
     * As an organizer, I want to be able to update an event poster
     */
    @Test
    public void testUpdatePoster() throws InterruptedException {
        // Update an event poster
        onView(withId(R.id.OrganizerSignInButton)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.add_event_button)).perform(click());
        onView(withId(R.id.event_name)).perform(replaceText("Test Event"));
        onView(withId(R.id.location)).perform(typeText("Test Location"));
        onView(withId(R.id.choose_date)).perform(click());
        //onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(2023, 12, 31));
        //onView(withId(android.R.id.button1)).perform(click());
        onView(withId(R.id.choose_date)).perform(typeText("2023-12-31"));
        onView(withId(R.id.choose_time)).perform(replaceText("12:00"));
        onView(withId(R.id.waiting_list_capacity)).perform(replaceText("50"));
        onView(withId(R.id.registration_until)).perform(replaceText("2023-12-31"));
        onView(withId(R.id.next_button)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.upload_poster_button)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.create_event_button)).perform(click());
        Thread.sleep(2000);

        onView(withId(R.id.event_list_view)).perform(click());
        Thread.sleep(2000);
        // no update event poster functionality yet

    }

    /**
     * Test for US 02.05.02
     * As an organizer, I want to be able to perform the "lottery"
     */
    @Test
    public void testLottery() {
        // Perform the lottery

    }

    /**
     * Test for US 02.05.03
     * As an organizer, I want to be able to draw replacement applicants
     */
    @Test
    public void testDrawReplacement() {
        // Draw replacement applicants
    }

    /**
     * Test for US 02.06.03
     * As an organizer, I want to be able to view the final list of entrants who enrolled for the event
     */
    @Test
    public void testViewFinalList() throws InterruptedException {
        // View the final list of entrants
        onView(withId(R.id.OrganizerSignInButton)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.add_event_button)).perform(click());
        onView(withId(R.id.event_name)).perform(replaceText("Test Event"));
        onView(withId(R.id.location)).perform(typeText("Test Location"));
        onView(withId(R.id.choose_date)).perform(click());
        //onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(2023, 12, 31));
        //onView(withId(android.R.id.button1)).perform(click());
        onView(withId(R.id.choose_date)).perform(typeText("2023-12-31"));
        onView(withId(R.id.choose_time)).perform(replaceText("12:00"));
        onView(withId(R.id.waiting_list_capacity)).perform(replaceText("50"));
        onView(withId(R.id.registration_until)).perform(replaceText("2023-12-31"));
        onView(withId(R.id.next_button)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.create_event_button)).perform(click());
        Thread.sleep(2000);
        
        onView(withText("Test Event")).check(matches(isDisplayed()));
        onView(withText("2023-12-31 12:00")).check(matches(isDisplayed()));
        onView(withText("Test Location")).check(matches(isDisplayed()));
    }

    /**
     * US 02.07.01 Allow organizers to send notifications to all entrants on the waiting list
     */
    @Test
    public void testSendNotifications() {
        // Send notifications to all entrants on the waiting list
    }
}
