package modeltests;

import android.net.Uri;

import com.example.espresso.Event.Event;
import com.example.espresso.Organizer.Facility;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.android.gms.tasks.TaskCompletionSource;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;

public class EventTest {

    private Event event;
    private Facility facility;

    @Mock
    private FirebaseStorage mockFirebaseStorage;

    @Mock
    private StorageReference mockStorageReference;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);  // Use openMocks instead of initMocks

        // Setup mock Facility
        facility = new Facility("Main Hall");

        // Create a new Event with mock data
        event = new Event("Concert", "2024-12-01", "19:00", "A great concert", "2024-11-30", 500, facility, 5, "Open", false, 10);
    }

    @Test
    public void testGetId() {
        // Test if the ID is generated correctly based on name, facility, and time
        String expectedId = "ConcertMain Hall19:00";
        assertEquals(expectedId, event.getId());
    }

    @Test
    public void testGetFacility() {
        // Test if the correct facility name is returned
        assertEquals("Main Hall", event.getFacility());
    }

    @Test
    public void testGetName() {
        // Test if the event name is correct
        assertEquals("Concert", event.getName());
    }

    @Test
    public void testGetDate() {
        // Test if the event date is correct
        assertEquals("2024-12-01", event.getDate());
    }

    @Test
    public void testGetTime() {
        // Test if the event time is correct
        assertEquals("19:00", event.getTime());
    }

    @Test
    public void testGetDescription() {
        // Test if the event description is correct
        assertEquals("A great concert", event.getDescription());
    }

    @Test
    public void testGetDeadline() {
        // Test if the event deadline is correct
        assertEquals("2024-11-30", event.getDeadline());
    }

    @Test
    public void testGetCapacity() {
        // Test if the event capacity is correct
        assertEquals(500, event.getCapacity());
    }

    @Test
    public void testGetDrawn() {
        // Test if the event drawn count is correct
        assertEquals(5, event.getDrawn());
    }

    @Test
    public void testGetStatus() {
        // Test if the event status is correct
        assertEquals("Open", event.getStatus());
    }

    @Test
    public void testGetGeolocation() {
        // Test if the event geolocation setting is correct
        assertFalse(event.getGeolocation());
    }

    @Test
    public void testGetSample() {
        // Test if the event sample parameter is correct
        assertEquals(10, event.getSample());
    }

    @Test
    public void testGetUrl_Success() {
        // Simulate successful URL fetch from Firebase using TaskCompletionSource
        TaskCompletionSource<Uri> taskCompletionSource = new TaskCompletionSource<>();
        Uri mockUri = Uri.parse("https://example.com/poster.jpg");
        taskCompletionSource.setResult(mockUri);  // Simulate success

        // Mock Firebase Storage and StorageReference
        Mockito.when(mockFirebaseStorage.getReference()).thenReturn(mockStorageReference);
        Mockito.when(mockStorageReference.child("posters").child(event.getId()).getDownloadUrl())
                .thenReturn(taskCompletionSource.getTask());

        final String[] fetchedUrl = new String[1];
        event.getUrl(url -> fetchedUrl[0] = url);  // Call getUrl

        assertNotNull(fetchedUrl[0]);
        assertEquals("https://example.com/poster.jpg", fetchedUrl[0]);
    }

    @Test
    public void testGetUrl_Failure() {
        // Simulate a failure when fetching the URL using TaskCompletionSource
        TaskCompletionSource<Uri> taskCompletionSource = new TaskCompletionSource<>();
        taskCompletionSource.setException(new Exception("Failed to fetch URL"));  // Simulate failure

        // Mock Firebase Storage and StorageReference
        Mockito.when(mockFirebaseStorage.getReference()).thenReturn(mockStorageReference);
        Mockito.when(mockStorageReference.child("posters").child(event.getId()).getDownloadUrl())
                .thenReturn(taskCompletionSource.getTask());

        final String[] fetchedUrl = new String[1];
        event.getUrl(url -> fetchedUrl[0] = url);  // Call getUrl

        assertNull(fetchedUrl[0]);  // Ensure URL is null when there's an error
    }
}
