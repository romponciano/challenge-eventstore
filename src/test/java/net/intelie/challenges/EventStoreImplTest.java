package net.intelie.challenges;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class EventStoreImplTest {
  EventStore store;  
  EventIterator outputIterator;

  @Before
  public void init() {
    store = new EventStoreImpl();
  };

  /**
   * Test if query return correctly with specific range
   */
  @Test(expected = IllegalStateException.class)
  public void query_valueBetween1And3_shouldReturn1And2() {
    String type = "type1";
    Event event1 = new Event(type, 1l);
    Event event2 = new Event(type, 2l);
    Event event3 = new Event(type, 3l);
    store.insert(event1);
    store.insert(event2);
    store.insert(event3);
    outputIterator = store.query(type, 1l, 3l);
    outputIterator.moveNext();
    assertEquals(event2, outputIterator.current());
    outputIterator.moveNext();
    assertEquals(event1, outputIterator.current());
    outputIterator.moveNext();
    outputIterator.current();
  }

  /**
   * Test if remove just remove the selected type
   */
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
    outputIterator = store.query(removeType, Long.MIN_VALUE, Long.MAX_VALUE);
    assertEquals(null, outputIterator);
    outputIterator = store.query(maintainType, 1l, 3l);
    outputIterator.moveNext();
    assertEquals(event3, outputIterator.current());
  }

  /**
   * Test if insert create a new ConcurrentHashTable for each
   * new type
   */
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

  /**
   * Test if query with null event return null query
   */
  @Test
  public void query_noEventType_shouldReturnNullIfQuery() {
    assertEquals(null, store.query("type", 1l, 3l));
  };
}