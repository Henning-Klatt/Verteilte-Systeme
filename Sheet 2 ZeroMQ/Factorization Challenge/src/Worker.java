import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.math.BigInteger;

public class Worker extends Thread {

    public void run() {
        ZContext context = new ZContext();

        // Create PULL socket to pull challenge from controller
        ZMQ.Socket socket_pull = context.createSocket(SocketType.PULL);
        // Connect to the controller PUSH socket
        socket_pull.connect("tcp://localhost:12345");

        // Create PUSH socket to send result back to controller
        ZMQ.Socket socket_push = context.createSocket(SocketType.PUSH);
        // Connect to the controller PULL socket
        socket_push.connect("tcp://localhost:12346");

        System.out.println("[Worker connected]");

        while(!Thread.currentThread().isInterrupted()){
            // wait for next request from the controller
            String request = socket_pull.recvStr(0);
            System.out.println("[Worker] Received PULL message from controller: " + new String(request));

            BigInteger[] result = Fermat.fermatFactorization(new BigInteger(request));
            System.out.println("[Worker] " + request + " = " + result[0] + " * " + result[1]);

            //Send result back to controller via PUSH socket
            socket_push.send((request + ":" + result[0] + ":" + result[1]).getBytes());
        }
    }
}
