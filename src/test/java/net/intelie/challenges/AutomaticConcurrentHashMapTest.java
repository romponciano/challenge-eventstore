package net.intelie.challenges;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AutomaticConcurrentHashMapTest {

    AutomaticConcurrentHashMap concurrenceMap;

    @Before
    public void init() {
        concurrenceMap = new AutomaticConcurrentHashMap();
    }

    /**
     * Test if construct create a correct instance of ConcurrentHashMap
     */
    @Test
    public void constructor_valid_shouldCreateInstanceOfConcurrentHashMap() {
        assertTrue(concurrenceMap instanceof ConcurrentHashMap);
    };

    /**
     * Test if autokeyput insert a new row with the correct hash
     */
    @Test
    public void autoKeyPut_validEvent_shouldInsertCorrectEvent() {
        long timestamp = 12l;
        String type = "type1";
        Event newEvent = new Event(type, timestamp);
        String hash = timestamp+type;
        concurrenceMap.autoKeyPut(newEvent);
        assertEquals(newEvent, concurrenceMap.get(hash));
    };

    /**
     * Test if autoKeyPut with null just ignores and
     * don't insert anything neither throw exceptions
     */
    @Test
    public void autoKeyPut_nullEvent_shouldNotAdd() {
        concurrenceMap.autoKeyPut(null);
        assertTrue(concurrenceMap.isEmpty());
    }
}
