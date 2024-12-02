package com.example.espresso.modeltests;

import com.example.espresso.Event.Event;
import com.example.espresso.Organizer.Facility;
import org.junit.Test;


import static org.junit.Assert.*;

public class EventTest {

    private Event mockEvent() {
        Facility facility = new Facility("Main Hall");
        Event mockEvent = new Event("Concert", "2024-12-01", "19:00", "A great concert", "2024-11-30", 500, facility, 5, "Open", false, 10);
        return mockEvent;
    }

    /**
     * Test the Event model facility.
     */
    @Test
    public void testGetFacility() {
        // Test if the correct facility name is returned
        Event event = mockEvent();
        assertEquals("Main Hall", event.getFacility());
    }

    /**
     * Test the Event model name.
     */
    @Test
    public void testGetName() {
        // Test if the event name is correct
        Event event = mockEvent();
        assertEquals("Concert", event.getName());
    }

    /**
     * Test the Event model date.
     */
    @Test
    public void testGetDate() {
        // Test if the event date is correct
        Event event = mockEvent();
        assertEquals("2024-12-01", event.getDate());
    }

    /**
     * Test the Event model time.
     */
    @Test
    public void testGetTime() {
        // Test if the event time is correct
        Event event = mockEvent();
        assertEquals("19:00", event.getTime());
    }

    /**
     * Test the Event model description.
     */
    @Test
    public void testGetDescription() {
        // Test if the event description is correct
        Event event = mockEvent();
        assertEquals("A great concert", event.getDescription());
    }

    /**
     * Test the Event model deadline.
     */
    @Test
    public void testGetDeadline() {
        // Test if the event deadline is correct
        Event event = mockEvent();
        assertEquals("2024-11-30", event.getDeadline());
    }

    /**
     * Test the Event model capacity.
     */
    @Test
    public void testGetCapacity() {
        // Test if the event capacity is correct
        Event event = mockEvent();
        assertEquals(500, event.getCapacity());
    }

    /**
     * Test the Event model drawn.
     */
    @Test
    public void testGetDrawn() {
        // Test if the event drawn count is correct
        Event event = mockEvent();
        assertEquals(5, event.getDrawn());
    }

    /**
     * Test the Event model status.
     */
    @Test
    public void testGetStatus() {
        // Test if the event status is correct
        Event event = mockEvent();
        assertEquals("Open", event.getStatus());
    }

    /**
     * Test the Event model geolocation.
     */
    @Test
    public void testGetGeolocation() {
        // Test if the event geolocation setting is correct
        Event event = mockEvent();
        assertFalse(event.getGeolocation());
    }

    /**
     * Test the Event model sample.
     */
    @Test
    public void testGetSample() {
        // Test if the event sample parameter is correct
        Event event = mockEvent();
        assertEquals(10, event.getSample());
    }

}
