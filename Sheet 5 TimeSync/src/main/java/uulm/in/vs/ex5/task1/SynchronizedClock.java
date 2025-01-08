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
    // TODO

    BaseClock clock = new BaseClock();

    public AtomicLong getServerTime(ZMQ.Socket requestSocket){
        requestSocket.send("".getBytes(ZMQ.CHARSET), 0);
        byte[] reply = requestSocket.recv(0);
        String string = new String(reply, ZMQ.CHARSET);
        long start = Long.parseLong(string);
        AtomicLong counter = new AtomicLong(start);
        System.out.println("Server sent time: " + counter.get());
        return counter;
    }


    public SynchronizedClock(ZContext context, String host, int numRequests) {

        ZMQ.Socket requestSocket = context.createSocket(SocketType.REQ);
        requestSocket.connect(host);

        AtomicInteger finishedRequests = new AtomicInteger(0);

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        int initDelay = 500;
        int period = 1000;
        Future<?> scheduledFuture = executor.scheduleAtFixedRate(() -> {

            if(finishedRequests.get() < numRequests) {
                AtomicLong servertime = getServerTime(requestSocket);
                clock.setTimeToFuture(servertime.get());
                finishedRequests.getAndIncrement();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
            }

            System.out.println(finishedRequests.get());
        }, initDelay, period, TimeUnit.MILLISECONDS);

        // TODO
    }

    // Konstruktor Uhr selbst zu initialisieren
    public SynchronizedClock(ZContext context, String host, int numRequests, long start) {
        // TODO
        clock.setTimeToFuture(start);

    }

    // Bestimmung des Abstands zu Ihrer derzeitigen Uhrzeit bieten
    public long getTime() { 
        // TODO
        return clock.getTime();
    }
}
