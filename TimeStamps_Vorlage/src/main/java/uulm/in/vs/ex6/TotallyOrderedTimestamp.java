package uulm.in.vs.time;

import java.math.BigInteger;

public class TotallyOrderedTimestamp implements Comparable<TotallyOrderedTimestamp> {

    long timestamp;
    int counter;

    public TotallyOrderedTimestamp(long timestamp, int counter) {
        this.timestamp = timestamp;
        this.counter = counter;
    }

    @Override
    public int compareTo(TotallyOrderedTimestamp arg) {
    	// TODO: define sorting logic

        int timestamp_diff = Long.compare(timestamp, arg.timestamp);
        // Auflösung der Timestamps reicht aus
        if(timestamp_diff != 0) {
            return timestamp_diff;
        }
        // Verwendung des Counters
        return this.counter - arg.counter;
    }

    public BigInteger asBigInteger() {
        // ToDo: selbe Ordnung abbilden wie die Zeitstempel
        return BigInteger.valueOf(timestamp);
    }

    public long getTimestamp() {
        return timestamp;
    }
}
