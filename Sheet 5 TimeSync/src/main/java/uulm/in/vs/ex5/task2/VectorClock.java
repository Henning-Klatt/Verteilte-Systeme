package uulm.in.vs.ex5.task2;

import java.util.Collection;
import java.util.Optional;

public class VectorClock {
        // TODO

    public VectorClock(Collection<Long> C, int id) {
        // TODO
    }

    public VectorClock(int size, int id){
        // TODO
    }

    /**
    * Returns all times in the vector
    */
    public long[] getTime() {
        // TODO
        return new long[0];
    }

    /**
    * Also returns incremented time for own processID
    */
    public long increment() {
        // TODO
        return 0;
    }

    /**
    * Returns time of given id
    */
    public long getTime(int id) {
        // TODO
        return 0;
    }

    public long merge(VectorClock b) throws IllegalArgumentException{
        // TODO
        return 0;
    }

    public long size() {
        // TODO
        return 0;
    }

    /**
    * Greater-or-Equals comparison
    * IllegalArgumentException is thrown when vectors are of different size.
    */
    public boolean geq(VectorClock b) throws IllegalArgumentException {
        // TODO
        return false;
    }

    /**
     *
     * @return Positive if a>b, Negative if a<b, 0 if a==b, empty Optional if not ordered
     * @throws IllegalArgumentException If Vectors are of different size
     */
    public static Optional<Integer> compare(VectorClock a, VectorClock b) throws IllegalArgumentException {
        // TODO
        return Optional.empty();
    }

    public boolean equals(VectorClock b) {
        // TODO
        return false;
    }
}
