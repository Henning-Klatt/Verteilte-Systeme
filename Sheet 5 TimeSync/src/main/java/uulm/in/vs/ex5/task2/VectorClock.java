package uulm.in.vs.ex5.task2;

import java.util.*;

public class VectorClock {

    List<Long> vector;
    int processID;

    public VectorClock(Collection<Long> C, int id) {
        this.vector = (List<Long>) C;
        this.processID = id;
    }

    public VectorClock(int size, int id){
        this.vector = new ArrayList<Long>(size);

        for (int i = 0; i < size; i++) {
            this.vector.add(0L);
        }

        this.processID = id;
    }

    /**
    * Returns all times in the vector
    */
    public long[] getTime() {
        return vector.stream().mapToLong(l -> l).toArray();
    }

    /**
    * Also returns incremented time for own processID
    */
    public long increment() {
        this.vector.set(this.processID, this.getTime(this.processID) + 1);
        return this.getTime(this.processID);
    }

    /**
    * Returns time of given id
    */
    public long getTime(int id) {
        return this.vector.get(id);
    }

    public long merge(VectorClock b) throws IllegalArgumentException{
        if(this.vector.size() != b.vector.size()) throw new IllegalArgumentException();

        ArrayList<Long> result = new ArrayList<Long>(b.vector.size());

        for(int i = 0; i < this.vector.size(); i++){
            // If own component
            if(i == this.processID){
                if(b.getTime(i) >= this.getTime(i)){
                    result.add(b.getTime(i)+1);
                } else{
                    result.add(this.getTime(i)+1);
                }
            }
            // Other components
            else{
                if(b.getTime(i) >= this.getTime(i)){
                    result.add(b.getTime(i));
                } else{
                    result.add(this.getTime(i));
                }
            }
        }
        this.vector = result;
        return result.get(this.processID);
    }

    public long size() {
        return this.vector.size();
    }

    /**
    * Greater-or-Equals comparison
    * IllegalArgumentException is thrown when vectors are of different size.
    */
    public boolean geq(VectorClock b) throws IllegalArgumentException {
        if(this.vector.size() != b.vector.size()) throw new IllegalArgumentException();

        for(int i = 0; i < this.vector.size(); i++){
            if(this.getTime(i) < b.getTime(i)) return false;
        }
        return true;
    }

    /**
     *
     * @return Positive if a>b, Negative if a<b, 0 if a==b, empty Optional if not ordered
     * @throws IllegalArgumentException If Vectors are of different size
     */
    public static Optional<Integer> compare(VectorClock a, VectorClock b) throws IllegalArgumentException {
        if(a.vector.size() != b.vector.size()) throw new IllegalArgumentException();

        Optional<Integer> result = Optional.empty();
        if (a.equals(b)) {
            return Optional.of(0);
        }

        int size = 0;
        for(int i = 0; i < a.vector.size(); i++){
            if(a.getTime(i) > b.getTime(i)){
                size += a.getTime(i)-b.getTime(i);
            } else{
                size -= b.getTime(i)-a.getTime(i);
            }
        }
        if(size == 0) return Optional.empty();

        return Optional.of(size);
    }

    public boolean equals(VectorClock b) {
        if(this.vector.size() != b.vector.size()) return false;

        for(int i = 0; i < this.vector.size(); i++){
            if(!Objects.equals(this.vector.get(i), b.vector.get(i))) return false;
        }

        return true;
    }
}
