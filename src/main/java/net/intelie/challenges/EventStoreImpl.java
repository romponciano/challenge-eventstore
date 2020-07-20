package net.intelie.challenges;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EventStoreImpl implements EventStore {  

  private final ConcurrentHashMap<String, List<ConcurrentHashMap<String, Event>>> events = new ConcurrentHashMap<>();

  /**
     * Stores an event
     *
     * @param event
     */
    public void insert(Event event) {
      ConcurrentHashMap<String, Event> newEvent = new ConcurrentHashMap<>();
      newEvent.put(Utils.generateHashFromEvent(event), event);
      // if already have event type list, then just get and add to it
      if(events.contains(event.type())) {
        events.get(event.type()).add(newEvent);
      } 
      // else, create the new list and insert new event to it
      else {
        List<ConcurrentHashMap<String, Event>> newList = new ArrayList<>();
        newList.add(newEvent);
        events.put(event.type(), newList);
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
      if(events.contains(type)) {
        ConcurrentHashMap<String, Event> result = new ConcurrentHashMap<>();
        // get the list with all ConcurrentHashMaps of determinated type
        for(ConcurrentHashMap<String, Event> typeHash : events.get(type)) {
          // for each entry in list, check if timestamp is between 
          // startTime (inclusive) and endTime (exclusive)
          for(Map.Entry<String, Event> entry : typeHash.entrySet()) {
            Event auxEvent = entry.getValue();
            long timestamp = auxEvent.timestamp();
            if(timestamp >= startTime && timestamp < endTime) {
              result.put(Utils.generateHashFromEvent(auxEvent), entry.getValue());
            };
          };
        };
        return new EventIteratorImpl(result);
      };
      // if events don't have type key, so just return null
      return null;
    };
}