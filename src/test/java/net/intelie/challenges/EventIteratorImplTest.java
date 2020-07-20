package net.intelie.challenges;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EventIteratorImplTest {

    EventIteratorImpl eventIterator;
    AutomaticConcurrentHashMap concurrentMap;
    Event event;

    @Before
    public void init() {
        concurrentMap = new AutomaticConcurrentHashMap();
        event = new Event("type1", Long.MAX_VALUE);
    };

    /**
     * Test if current() before moveNext() throws 
     * IllegalStateException
     */
    @Test(expected = IllegalStateException.class)
    public void current_beforeMoveNext_shouldThrowIllegalState() {
        concurrentMap.autoKeyPut(event);
        eventIterator = new EventIteratorImpl(concurrentMap);
        eventIterator.current();
    }

    /**
     * Test if current() throws IllegalStateException if
     * it does not have next()
     */
    @Test(expected = IllegalStateException.class)
    public void current_noSecondEvent_shouldThrowIllegalState() {
        concurrentMap.autoKeyPut(event);
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
        concurrentMap.autoKeyPut(event);
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
        concurrentMap.autoKeyPut(event);
        // Long.MIN to 'be different from previous one
        Event event2 = new Event("type2", Long.MIN_VALUE); 
        concurrentMap.autoKeyPut(event2);
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
        concurrentMap.autoKeyPut(event);
        eventIterator = new EventIteratorImpl(concurrentMap);
        eventIterator.moveNext();
        Event currEvent = eventIterator.current();
        assertEquals(event.timestamp(), currEvent.timestamp());
        assertEquals(event.type(), currEvent.type());
    };

    /**
     * Test if remove works and does not call moveNext()
     */
    @Test(expected = IllegalStateException.class)
    public void remove_validEvent_shouldRemoveAndNotMoveNext() {
        concurrentMap.autoKeyPut(event);
        eventIterator = new EventIteratorImpl(concurrentMap);
        eventIterator.moveNext();
        eventIterator.remove();
        eventIterator.current();
    };

    /**
     * Test if remove throws IllegalStateException if
     * selectedKey is null
     */
    @Test(expected = IllegalStateException.class)
    public void remove_selectKeyNull_shouldThrowIllegalState() {
        eventIterator = new EventIteratorImpl(null);
        eventIterator.moveNext();
        eventIterator.remove();
    };

     /**
     * Test if call moveNext works after remove
     */
    @Test
    public void moveNext_validEvent_shouldWorkAfterRemove() {
        Event event2 = new Event("teste2", 1l);
        concurrentMap.autoKeyPut(event);
        concurrentMap.autoKeyPut(event2);        
        eventIterator = new EventIteratorImpl(concurrentMap);
        eventIterator.moveNext();
        eventIterator.remove();
        eventIterator.moveNext();
        assertEquals(event, eventIterator.current());
    };

    /**
     * Test if close method really close and throws
     * IllegalState if current() is called
     */
    @Test(expected = IllegalStateException.class)
    public void close_current_shouldThrowExceptionAfterNewTry() throws Exception {
        eventIterator = new EventIteratorImpl(null);
        eventIterator.close();
        assertEquals(false, eventIterator.moveNext());
        eventIterator.current();
    }

    /**
     * Test if close method really close and throws
     * IllegalState if remove() is called
     */
    @Test(expected = IllegalStateException.class)
    public void close_remove_shouldThrowExceptionAfterNewTry() throws Exception {
        eventIterator = new EventIteratorImpl(null);
        eventIterator.close();
        assertEquals(false, eventIterator.moveNext());
        eventIterator.remove();
    }
}