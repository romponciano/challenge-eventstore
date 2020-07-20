package net.intelie.challenges;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.ConcurrentHashMap;

public class EventIteratorImplTest {

    EventIteratorImpl eventIterator;
    ConcurrentHashMap<String, Event> concurrentMap;
    Event event;

    @Before
    public void init() {
        concurrentMap = new ConcurrentHashMap<String, Event>();
        event = new Event("type1", Long.MAX_VALUE);
    };

    /**
     * Test if current() before moveNext() throws 
     * IllegalStateException
     */
    @Test(expected = IllegalStateException.class)
    public void current_beforeMoveNext_shouldThrowIllegalState() {
        concurrentMap.put(Utils.generateHashFromEvent(event), event);
        eventIterator = new EventIteratorImpl(concurrentMap);
        eventIterator.current();
    }

    /**
     * Test if current() throws IllegalStateException if
     * it does not have next()
     */
    @Test(expected = IllegalStateException.class)
    public void current_noSecondEvent_shouldThrowIllegalState() {
        concurrentMap.put(Utils.generateHashFromEvent(event), event);
        eventIterator = new EventIteratorImpl(concurrentMap);
        eventIterator.moveNext();
        eventIterator.moveNext();
        eventIterator.current();
    }

    /**
     * Test if moveNext() when it does not have next, return false
     */
    @Test
    public void moveNext_noSecondEvent_shouldNotHaveNext() {
        concurrentMap.put(Utils.generateHashFromEvent(event), event);
        eventIterator = new EventIteratorImpl(concurrentMap);
        eventIterator.moveNext();
        assertEquals(false, eventIterator.moveNext());
    }

    /**
     * Test if moveNext() works to get the second event.
     * This method also checks if current works properly
     */
    @Test
    public void moveNext_validTwoEvents_shouldGet() {
        concurrentMap.put(Utils.generateHashFromEvent(event), event);
        // Long.MIN to 'be different from previous one
        Event event2 = new Event("type2", Long.MIN_VALUE); 
        concurrentMap.put(Utils.generateHashFromEvent(event2), event2);
        eventIterator = new EventIteratorImpl(concurrentMap);
        eventIterator.moveNext();
        eventIterator.moveNext();
        Event currEvent = eventIterator.current();
        assertEquals(event2.timestamp(), currEvent.timestamp());
        assertEquals(event2.type(), currEvent.type());
    }

    /**
     * Test if constructor does not have next if it
     * was constructed with null concurrentMap
     */
    @Test
    public void constructor_null_shouldNotHaveNext() {
        eventIterator = new EventIteratorImpl(null);
        assertEquals(false, eventIterator.moveNext());
    };

    /**
     * Test if constructor works correctly. This test need
     * functions moveNext(), current() and close() to works
     * properly
     */
    @Test
    public void constructor_validEvent_shouldConstructCorrectly() {
        concurrentMap.put(Utils.generateHashFromEvent(event), event);
        eventIterator = new EventIteratorImpl(concurrentMap);
        eventIterator.moveNext();
        Event currEvent = eventIterator.current();
        assertEquals(event.timestamp(), currEvent.timestamp());
        assertEquals(event.type(), currEvent.type());
    };
}