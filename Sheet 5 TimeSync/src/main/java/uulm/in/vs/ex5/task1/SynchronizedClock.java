package uulm.in.vs.ex5.task1;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author David Mödinger
 * 
 */

public class SynchronizedClock implements Clock{

    BaseClock clock = new BaseClock();

    public AtomicLong getServerTime(ZMQ.Socket requestSocket){
        requestSocket.send("".getBytes(ZMQ.CHARSET), 0);
        byte[] reply = requestSocket.recv(0);
        String string = new String(reply, ZMQ.CHARSET);
        long start = Long.parseLong(string);
        AtomicLong counter = new AtomicLong(start);
        //System.out.println("Server sent time: " + counter.get());
        return counter;
    }

    // Constructor requests server time
    public SynchronizedClock(ZContext context, String host, int numRequests) {

        ZMQ.Socket requestSocket = context.createSocket(SocketType.REQ);
        requestSocket.connect("tcp://" + host);

        AtomicInteger finishedRequests = new AtomicInteger(0);

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        int initDelay = 500;
        int period = 1000;
        Future<?> scheduledFuture = executor.scheduleAtFixedRate(() -> {

            if(finishedRequests.get() < numRequests) {
                AtomicLong servertime = getServerTime(requestSocket);
                clock.setTimeToFuture(servertime.get() + (2/2));
                finishedRequests.getAndIncrement();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
            }
        }, initDelay, period, TimeUnit.MILLISECONDS);
    }

    // Constructor initialises with given start time value
    public SynchronizedClock(ZContext context, String host, int numRequests, long start) {
        // TODO
        clock.setTimeToFuture(start);
        ZMQ.Socket requestSocket = context.createSocket(SocketType.REQ);
        requestSocket.connect("tcp://" + host);

        AtomicInteger finishedRequests = new AtomicInteger(0);

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        int initDelay = 500;
        int period = 1000;
        Future<?> scheduledFuture = executor.scheduleAtFixedRate(() -> {

            if(finishedRequests.get() < numRequests) {
                long diff = getDiff(getServerTime(requestSocket));

                if(diff > 1000000000) {
                    clock.setVeryFastSpeed();
                    clock.increaseSpeed();
                } else if(diff > -1000000000) {
                    clock.setVerySlowSpeed();
                    clock.decreaseSpeed();
                } else if(diff > 100000) {
                    clock.setFastSpeed();
                    clock.increaseSpeed();
                } else if(diff > -100000) {
                    clock.setSlowSpeed();
                    clock.decreaseSpeed();
                } else if(diff > 1000) {
                    clock.setNormalSpeed();
                }

                finishedRequests.getAndIncrement();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
            }
        }, initDelay, period, TimeUnit.MILLISECONDS);

    }

    // Difference between given and internal time
    public long getDiff(AtomicLong input) {
        return input.get() - clock.getTime();
    }

    public long getTime() {
        return clock.getTime();
    }
}
