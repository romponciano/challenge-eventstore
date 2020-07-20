package net.intelie.challenges;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

public class EventIteratorImpl implements EventIterator {
    private final ConcurrentHashMap<Long, Event> map;
    private final Iterator<Long> mapOrder;
    private Long selectedKey = null;

    public EventIteratorImpl(ConcurrentHashMap<Long, Event> map) {
        this.map = map;
        // if map is null or empty, than it does not have an order
        if (this.map == null || this.map.isEmpty()) {
            this.mapOrder = null;
        }
        // else, set mapOrder = iterator of map keySet
        else {
            this.mapOrder = this.map.keySet().iterator();
        };
    }

    /**
     * Move the iterator to the next event, if any.
     *
     * @return false if the iterator has reached the end, true otherwise.
     */
    @Override
    public boolean moveNext() {
        // if mapOrder null or mapOrder does not have next, then it ends
        if (mapOrder == null || !mapOrder.hasNext()) {
            selectedKey = null;
            return false;
        };
        // else, set selectedKey to next
        selectedKey = mapOrder.next();
        return true;
    }

    /**
     * Gets the current event ref'd by this iterator.
     *
     * @return the event itself.
     * @throws IllegalStateException if {@link #moveNext} was never called
     *                               or its last result was {@code false}.
     */
    @Override
    public Event current() {
        // if selectedKey is null, then it does not have current
        if (selectedKey == null) {
            throw new IllegalStateException();
        };
        // else, get current
        return map.get(selectedKey);
    }

    /**
     * Remove current event from its store.
     *
     * @throws IllegalStateException if {@link #moveNext} was never called
     *                               or its last result was {@code false}.
     */
    @Override
    public void remove() {
        // if selectedKey is null, then it does not have current
        if (selectedKey == null) {
            throw new IllegalStateException();
        };
        // else, remove current
        map.remove(selectedKey);
    }

    /**
     * Method to close this resource. Override from AutoCloseable.class
     * 
     * The close() method of an AutoCloseable object is called automatically 
     * when exiting a try-with-resources block for which the object has been 
     * declared in the resource specification header.
     */
    @Override
    public void close() throws Exception {
        // if all conditions stops with selectedKey == null, 
        // then just set selectedKey = null to close
        selectedKey = null;
    }
}