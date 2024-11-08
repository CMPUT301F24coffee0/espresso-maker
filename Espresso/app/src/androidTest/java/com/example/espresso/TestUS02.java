package com.example.espresso;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@LargeTest
public class TestUS02 extends UiTest{
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);


    //US 02.01.01 Allow organizers to create new events & generate a promo QR code
    @Test
    public void testCreateEvent() throws InterruptedException {
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
        onView(withId(R.id.attendees_to_sample)).perform(replaceText("10"));
        onView(withId(R.id.next_button)).perform(click());

        onView(withId(R.id.event_name)).check(matches(withText("Test Event")));
        onView(withId(R.id.location)).check(matches(withText("Test Location")));
        onView(withId(R.id.choose_date)).check(matches(withText("2023-12-31")));
        onView(withId(R.id.choose_time)).check(matches(withText("12:00")));
        onView(withId(R.id.waiting_list_capacity)).check(matches(withText("50")));
        onView(withId(R.id.registration_until)).check(matches(withText("2023-12-31")));
        onView(withId(R.id.attendees_to_sample)).check(matches(withText("10")));
    }

    @Test
    public void testGenerateQRCode() throws InterruptedException {
        // Generate a QR code for the event
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
        onView(withId(R.id.attendees_to_sample)).perform(replaceText("10"));
        onView(withId(R.id.next_button)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.create_event_button)).perform(click());
        Thread.sleep(2000);

        onView(withId(R.id.event_list_view)).perform(click());
        Thread.sleep(2000);
        onView(withText(R.id.share_button)).perform(click());
        Thread.sleep(2000);
        onView(withText("Share QR Code")).check(matches(isDisplayed()));

    }

    //US 02.01.02 Allow the organizer to store hash data of the generated QR in the database
    @Test
    public void testStoreHashData() {
        // Store hash data in the database

    }

    //US 02.01.03 Allow organizers to create and manage their facility profile.
    @Test
    public void testCreateFacilityProfile() {
        // Create a new facility profile
    }

    //US 02.02.01 Allow organizers to view the list of entrants on the waiting list
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
        onView(withId(R.id.attendees_to_sample)).perform(replaceText("10"));
        onView(withId(R.id.next_button)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.create_event_button)).perform(click());
        Thread.sleep(2000);

        onView(withId(R.id.event_list_view)).perform(click());
        Thread.sleep(2000);
        onView(withText("Test Event")).check(matches(isDisplayed()));
        onView(withText("2023-12-31 12:00")).check(matches(isDisplayed()));
        onView(withText("Test Location")).check(matches(isDisplayed()));
    }

    //US 02.04.01 Allow an organizer to upload an event poster
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
        onView(withId(R.id.attendees_to_sample)).perform(replaceText("10"));
        onView(withId(R.id.next_button)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.upload_poster_button)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.create_event_button)).perform(click());
        Thread.sleep(2000);
        onView(withText("Test Event")).check(matches(isDisplayed()));
        onView(withText("2023-12-31 12:00")).check(matches(isDisplayed()));
        onView(withText("Test Location")).check(matches(isDisplayed()));
    }

    //US 02.04.02 Allow an organizer to update an event poster
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
        onView(withId(R.id.attendees_to_sample)).perform(replaceText("10"));
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

    //US 02.05.02 Allow organizers to perfom the "lottery"
    @Test
    public void testLottery() {
        // Perform the lottery

    }

    //US 02.05.03 Allow organizers to be able to draw replacement applicants
    @Test
    public void testDrawReplacement() {
        // Draw replacement applicants
    }

    //US 02.06.03 Allow organizers to see a final list of entrants who enrolled for the event
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
        onView(withId(R.id.attendees_to_sample)).perform(replaceText("10"));
        onView(withId(R.id.next_button)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.create_event_button)).perform(click());
        Thread.sleep(2000);

//        onView(withId(R.id.name)).check(matches(withText("Test Event")));
//        onView(withId(R.id.date)).check(matches(withText("2023-12-31")));
//        onView(withId(R.id.location)).check(matches(withText("Test Location")));
        onView(withText("Test Event")).check(matches(isDisplayed()));
        onView(withText("2023-12-31 12:00")).check(matches(isDisplayed()));
        onView(withText("Test Location")).check(matches(isDisplayed()));
    }

    //US 02.07.01 Allow organizers to send notifications to all entrants on the waiting list
    @Test
    public void testSendNotifications() {
        // Send notifications to all entrants on the waiting list
    }
}
