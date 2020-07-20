package net.intelie.challenges;

public class Utils {

  /**
   * Method to generate 'hash' timestamp+type from event
   * @param event
   * @return null if event is null. timestamp+type string
   * if valid event
   */
  public static String generateHashFromEvent(Event event) {
    if(event == null) {
      return null;
    };
    return event.timestamp() + event.type();
  };
  
}