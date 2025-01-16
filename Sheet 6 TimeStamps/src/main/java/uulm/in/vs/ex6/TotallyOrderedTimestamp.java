package uulm.in.vs.time;

import java.math.BigInteger;

public class TotallyOrderedTimestamp implements Comparable<TotallyOrderedTimestamp> {

    long timestamp;
    int counter;
    long clock_pid;

    public TotallyOrderedTimestamp(long timestamp, int counter, long clock_pid) {
        this.timestamp = timestamp;
        this.counter = counter;
        this.clock_pid = clock_pid;
    }

    @Override
    public int compareTo(TotallyOrderedTimestamp arg) {

        // Auflösung der Timestamps reicht aus
        int timestamp_diff = Long.compare(timestamp, arg.timestamp);
        if(timestamp_diff != 0) {
            return timestamp_diff;
        }

        // Auflösung eines clock counters reicht aus
        int counter_diff = Integer.compare(counter, arg.counter);
        if(counter_diff != 0) {
            return counter_diff;
        }

        // Verwendung der Clock PID
        return Long.compare(clock_pid, arg.clock_pid);
    }

    public BigInteger asBigInteger() {
        return BigInteger.valueOf(timestamp*100 + counter*10 + clock_pid);
    }

    public long getTimestamp() {
        return timestamp;
    }
}
