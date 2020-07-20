package net.intelie.challenges;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class EventStoreImplTest {
  EventStore store;  
  EventIterator outpuIterator;

  @Before
  public void init() {
    store = new EventStoreImpl();
  };

  @Test(expected = IllegalStateException.class)
  public void query_valueBetween1And3_shouldReturn1And2() {
    String type = "type1";
    Event event1 = new Event(type, 1l);
    Event event2 = new Event(type, 2l);
    Event event3 = new Event(type, 3l);
    store.insert(event1);
    store.insert(event2);
    store.insert(event3);
    outpuIterator = store.query(type, 1l, 3l);
    outpuIterator.moveNext();
    assertEquals(event2, outpuIterator.current());
    outpuIterator.moveNext();
    assertEquals(event1, outpuIterator.current());
    outpuIterator.moveNext();
    outpuIterator.current();
  }

  @Test
  public void remove_TwoEventsDifferentTypes_shouldRemoveOneType() {
    String removeType = "type1";
    String maintainType = "type2";
    Event event1 = new Event(removeType, 1l);
    Event event2 = new Event(removeType, 2l);
    Event event3 = new Event(maintainType, 2l);
    store.insert(event1);
    store.insert(event2);
    store.insert(event3);
    store.removeAll(removeType);
    outpuIterator = store.query(removeType, Long.MIN_VALUE, Long.MAX_VALUE);
    assertEquals(null, outpuIterator);
    outpuIterator = store.query(maintainType, 1l, 3l);
    outpuIterator.moveNext();
    assertEquals(event3, outpuIterator.current());
  }

  @Test
  public void insert_twoEventsSameType_shouldHaveOneListWithEvents() {
    String type = "type1";
    Event event1 = new Event(type, 1l);
    Event event2 = new Event(type, 2l);
    store.insert(event1);    
    store.insert(event2);
    EventIterator outputIterator = store.query(type, 1l, 3l);    
    outputIterator.moveNext();
    assertEquals(event2, outputIterator.current());
    outputIterator.moveNext();
    assertEquals(event1, outputIterator.current());
  };

  @Test
  public void query_noEventType_shouldReturnNullIfQuery() {
    assertEquals(null, store.query("type", 1l, 3l));
  };

  @Test
  public void query_noEventType_shouldReturnNullIfQueryNull() {
    assertEquals(null, store.query(null, 1l, 3l));
  }
}