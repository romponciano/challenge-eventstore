package net.intelie.challenges;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

public class EventIteratorImplTest {

    EventIteratorImpl eventIterator;
    ConcurrentHashMap<Long, Event> concurrentMap;
    Event event;

    @Before
    public void init() {
        concurrentMap = new ConcurrentHashMap<Long, Event>();
        event = new Event("type1", new Date().getTime());
    };

    /**
     * Test if current() before moveNext() throws 
     * IllegalStateException
     */
    @Test(expected = IllegalStateException.class)
    public void current_beforeMoveNext_shouldThrowIllegalState() {
        concurrentMap.put(1l, event);
        eventIterator = new EventIteratorImpl(concurrentMap);
        eventIterator.current();
    }

    /**
     * Test if current() throws IllegalStateException if
     * it does not have next()
     */
    @Test(expected = IllegalStateException.class)
    public void current_noSecondEvent_shouldThrowIllegalState() {
        concurrentMap.put(1l, event);
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
        concurrentMap.put(1l, event);
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
        concurrentMap.put(1l, event);
        Event event2 = new Event("type2", new Date().getTime());
        concurrentMap.put(2l, event2);
        eventIterator = new EventIteratorImpl(concurrentMap);
        eventIterator.moveNext();
        eventIterator.moveNext();
        assertEquals(event2, eventIterator.current());
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
        concurrentMap.put(1l, event);
        eventIterator = new EventIteratorImpl(concurrentMap);
        eventIterator.moveNext();
        assertEquals(event, eventIterator.current());
    };
}