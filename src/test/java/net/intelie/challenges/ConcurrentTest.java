package net.intelie.challenges;

/**
 * This class will show if store works with competition.
 * INSERTION:
 *      With 2 producers in 3 threads, the insertion can occur at the same
 *      time for threads {1 or 2} and 3. And in parallel between threads 1 and 2.
 *      Because tp1 and tp2 insert the same type of event (accessing the same key),
 *      while tp3 insert a different type.
 *
 * QUERY:
 *      With 2 consumers on 2 different threads consuming different types, the
 *      query of both can happen in parallel.
 *
 * MOTIVATION FOR CONCURRENT HASH MAP:
 *      I needed to store the data in some structure that was equally (or close) efficient in
 *      updating and querying, in addition to dealing with competition.
 *
 *      Having a key, the HashTable structure has query and update O(1). However, the HashTable
 *      class does not deal with competition. However, Java still offers two structures that work similarly
 *      to HashTable and that deal with competitions: SynchronizedHashMap and ConcurrentHashMap.
 *
 *      The main difference that motivated my choice for ConcurrentHashMap, is the fact that SynchronizedHashMap
 *      locks the entire HashTable when it receives any operation. While ConcurrentHashMap only locks the tuple
 *      being inserted/changed/removed/fetched. This means that the insertion of different keys can occur
 *      simultaneously (which would not happen in SynchronizedHashMap, generating performance overhead).
 *
 * WHY STORE NEED TO HAVE ANOTHER CONCURRENT HASH MAP?
 *      As the query method occurs primarily by the type of the event, and the removeAll method is entirely based
 *      on the type of the event, it would be necessary to search the ConcurrentHashMap for elements that satisfy
 *      the criteria, generating an O (n) complexity, where n is equal to the amount of total events.
 *      If ConcurrentHashMap accepted identical keys, I could solve the problem by storing the event type as a key.
 *      However, this is not possible.
 *
 *      So, I needed to create another ConcurrentHashMap that stores a ConcurrentHashMap of the events. So, in this
 *      "external" HashTable I can use the type as a key.
 *
 *      {consider external HashTable = HTE and internal HashTable = HTI}
 *      Thus, with each new insertion, I check if the type already exists at HTE. If so, I redeem this HTI with O(1)
 *      complexity using the type (in HTE) and insert the new event in HTI. If not, I simply create the new HTI for
 *      that new type and start storing the event in it.
 *      At the end of the insertion, I will have an HTE with keys by the type of the event where, each HTE tuple
 *      represents all the events of a certain type.
 *
 *      With this approach, removal occurs at O​​(1), as I remove all HTI from HTE using the type key.
 *
 *      The query will have complexity O(m), where m is equal to the total number of events of a given type. Because,
 *      after rescuing the HTI of a type, I need to go through the HTI entirely and check which events are in the
 *      informed range. Still, compared to the case without using a HTE, O(m) < O(n).
 *      reminder note: O(n), where n is equal to the number of total events
 */
public class ConcurrentTest {

    public static final EventStore store = new EventStoreImpl();

    public static void main(String[] args) throws InterruptedException {
        String type1 = "type1";
        String type2 = "type2";
        Producer p1 = new Producer(10, 200, type1);
        Producer p2 = new Producer(7, 200, type2);
        Consumer c1 = new Consumer(8, 100, type1);
        Consumer c2 = new Consumer(3, 200, type2);

        Thread tp1 = new Thread(p1, "TP1");
        Thread tp2 = new Thread(p1, "TP2-1");
        Thread tp3 = new Thread(p2, "TP3");
        Thread tc1 = new Thread(c1, "TC1");
        Thread tc2 = new Thread(c2, "TC2");

        tp1.start();
        tp2.start();
        tp3.start();
        while(  !tp1.getState().equals(Thread.State.TERMINATED) ||
                !tp2.getState().equals(Thread.State.TERMINATED) ||
                !tp3.getState().equals(Thread.State.TERMINATED)
        ) {
            Thread.sleep(150);
        }
        tc1.start();
        tc2.start();
    }
}


class Consumer implements Runnable {
    private final int maxConsume;
    private final long interval;
    private final String type;
    private EventIterator consumedIterator;

    public Consumer(int maxConsume, long interval, String type) {
        this.maxConsume = maxConsume;
        this.interval = interval;
        this.type = type;
        this.consumedIterator = null;
    }

    @Override
    public void run() {
        consumedIterator = ConcurrentTest.store.query(type, 0L, maxConsume+1);
        while(consumedIterator != null && consumedIterator.moveNext()) {
            Event currEvent = consumedIterator.current();
            String threadName = Thread.currentThread().getName();
            System.out.println(threadName + " Event{type: " +currEvent.type()+", timestamp: " +currEvent.timestamp()+"}");
            try {
                Thread.sleep(interval);
            } catch (Throwable e) {
                e.printStackTrace();
            };
        };
    };
}

class Producer implements Runnable {
    private final int maxVal;
    private final long interval;
    private final String type;

    Producer(int maxVal, long interval, String type) {
        this.maxVal = maxVal;
        this.interval = interval;
        this.type = type;
    }

    @Override
    public void run() {
        for (int i=0; i <= maxVal; i++) {
            ConcurrentTest.store.insert(new Event(type, i));
            String threadName = Thread.currentThread().getName();
            System.out.println(threadName + " inserting... " + type + " " + i);
            try {
                Thread.sleep(interval);
            } catch (Throwable e) {
                e.printStackTrace();
            };
        };
    };
}