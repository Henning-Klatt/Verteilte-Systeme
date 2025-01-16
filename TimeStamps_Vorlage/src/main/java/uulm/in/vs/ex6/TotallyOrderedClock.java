package uulm.in.vs.time;

import java.util.ArrayList;
import java.util.List;

public class TotallyOrderedClock {
    private long PID;

    List<TotallyOrderedTimestamp> timestamps = new ArrayList<TotallyOrderedTimestamp>();

    public TotallyOrderedClock(long PID) {
        this.PID = PID;
    }

    public TotallyOrderedTimestamp createTimestamp() throws IllegalArgumentException  {
        return createTimestamp(System.currentTimeMillis());
    }

    public TotallyOrderedTimestamp createTimestamp(long time) throws IllegalArgumentException {
        // Zurückliegende Zeit prüfen - Gibt es bereits einen kleineren Timestamp?
        for (TotallyOrderedTimestamp timestamp : timestamps) {
            if(time < timestamp.getTimestamp()){
                throw new IllegalArgumentException("Timestamp must be in the future");
            }
        }

        var tot = new TotallyOrderedTimestamp();
        tot.timestamp = time;
        timestamps.add(tot);

        return tot;
    }
}
