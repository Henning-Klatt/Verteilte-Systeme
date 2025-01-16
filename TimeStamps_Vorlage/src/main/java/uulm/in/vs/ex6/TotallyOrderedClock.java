package uulm.in.vs.time;

public class TotallyOrderedClock {
    private long PID;

    // TODO

    public TotallyOrderedClock(long PID) {
        this.PID = PID;
    }

    public TotallyOrderedTimestamp createTimestamp() throws IllegalArgumentException  {
        return createTimestamp(System.currentTimeMillis());
    }

    public TotallyOrderedTimestamp createTimestamp(long time) throws IllegalArgumentException {
    	// TODO
    }
}
