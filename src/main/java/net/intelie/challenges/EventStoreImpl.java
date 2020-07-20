package net.intelie.challenges;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EventStoreImpl implements EventStore {  

  private final ConcurrentHashMap<String, AutomaticConcurrentHashMap> events = new ConcurrentHashMap<>();

  /**
     * Stores an event
     *
     * @param event
     */
    public void insert(Event event) {
      // if already have event type, then just get and add to it
      if(events.containsKey(event.type())) {
        events.get(event.type()).autoKeyPut(event);
      } 
      // else, create the new concurrenthashmap and insert new event to it
      else {
        AutomaticConcurrentHashMap newEvent = new AutomaticConcurrentHashMap();
        newEvent.autoKeyPut(event);
        events.put(event.type(), newEvent);
      };
    };


    /**
     * Removes all events of specific type.
     *
     * @param type
     */
    public void removeAll(String type) {
      events.remove(type);
    };

    /**
     * Retrieves an iterator for events based on their type and timestamp.
     *
     * @param type      The type we are querying for.
     * @param startTime Start timestamp (inclusive).
     * @param endTime   End timestamp (exclusive).
     * @return An iterator where all its events have same type as
     * {@param type} and timestamp between {@param startTime}
     * (inclusive) and {@param endTime} (exclusive).
     */
    public EventIterator query(String type, long startTime, long endTime) {
      if(type != null && events.containsKey(type)) {
        AutomaticConcurrentHashMap result = new AutomaticConcurrentHashMap();
        // get the concurrenthashmap with all events of determinated type
        // for each entry in map, check if timestamp is between
        // startTime (inclusive) and endTime (exclusive)
        for(Map.Entry<String, Event> entry : events.get(type).entrySet()) {
          Event auxEvent = entry.getValue();
          long timestamp = auxEvent.timestamp();
          if(timestamp >= startTime && timestamp < endTime) {
            result.autoKeyPut(entry.getValue());
          };
        };
        return new EventIteratorImpl(result);
      };
      // if events don't have type key, so just return null
      return null;
    };
}