package net.intelie.challenges;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AutomaticConcurrenceHashMapTest {

    AutomaticConcurrenceHashMap concurrenceMap;

    @Before
    public void init() {
        concurrenceMap = new AutomaticConcurrenceHashMap();
    }

    @Test
    public void constructor_valid_shouldCreateInstanceOfConcurrentHashMap() {
        assertTrue(concurrenceMap instanceof ConcurrentHashMap);
    };

    @Test
    public void autoKeyPut_validEvent_shouldInsertCorrectEvent() {
        long timestamp = 12l;
        String type = "type1";
        Event newEvent = new Event(type, timestamp);
        String hash = timestamp+type;
        concurrenceMap.autoKeyPut(newEvent);
        assertEquals(newEvent, concurrenceMap.get(hash));
    };

    @Test
    public void autoKeyPut_nullEvent_shouldNotAdd() {
        concurrenceMap.autoKeyPut(null);
        assertTrue(concurrenceMap.isEmpty());
    }
}
