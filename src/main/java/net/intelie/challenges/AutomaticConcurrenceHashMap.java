package net.intelie.challenges;

import java.util.concurrent.ConcurrentHashMap;

public class AutomaticConcurrenceHashMap extends ConcurrentHashMap<String, Event> {

  private static final long serialVersionUID = -1292289496279862210L;

  public AutomaticConcurrenceHashMap() {
    super();
  }

  /**
   * Method to insert an event with automatic generated key
   * @param event
   */
  public void autoKeyPut(Event event) {
    this.put(generateHashFromEvent(event), event);
  }
 
  /**
   * Method to generate 'hash' timestamp+type from event
   * @param event
   * @return null if event is null. timestamp+type string
   * if valid event
   */
  private String generateHashFromEvent(Event event) {
    if(event == null) {
      return null;
    };
    return event.timestamp() + event.type();
  };
}