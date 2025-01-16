package uulm.in.vs.time;

import java.math.BigInteger;

public class TotallyOrderedTimestamp implements Comparable<TotallyOrderedTimestamp> {
	// TODO

    long timestamp;

    @Override
    public int compareTo(TotallyOrderedTimestamp arg) {
    	// TODO

        return 0;
    }

    public BigInteger asBigInteger() {
    	// TODO

        return BigInteger.valueOf(timestamp);
    }

    public long getTimestamp() {
        // TODO

        return timestamp;
    }
}
