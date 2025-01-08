package uulm.in.vs.ex5.task2;

public class LamportClock implements Comparable<LamportClock>{

    // eigene Logische Uhr
    long LC;

    public LamportClock() {
        this.LC = 0;
    }

    public LamportClock(long init) {
        this.LC = init;
    }

    public long getTime() {
        return LC;
    }

    /**
    * Also returns incremented time.
    */
    public long increment() {
        // lokales Ereignis: LC := LC + 1
        return this.LC += 1;
    }

    public long merge(LamportClock b) {
        if(b.getTime() > this.getTime()) {
            return b.getTime()+1;
        } else{
            return this.getTime()+1;
        }
    }

    public static LamportClock merge(LamportClock a, LamportClock b) {
        if(a.getTime() > b.getTime()) {
            return new LamportClock(a.getTime()+1);
        } else{
            return new LamportClock(b.getTime()+1);
        }
    }

    public static int compare(LamportClock a, LamportClock b) {
        return Long.valueOf(a.getTime()).compareTo(Long.valueOf(b.getTime()));
    }

    public boolean equals(LamportClock b) {
        if (this.getTime() == b.getTime()) {
            return true;
        }
        return false;
    }

    @Override
    public int compareTo(LamportClock l) {
        return Long.valueOf(this.getTime()).compareTo(Long.valueOf(l.getTime()));
    }
}
