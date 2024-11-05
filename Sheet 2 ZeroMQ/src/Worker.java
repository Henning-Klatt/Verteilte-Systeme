import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.math.BigInteger;


public class Worker {

    public static void main(String[] args) {
        ZContext context = new ZContext();

        // Create PULL socket to pull from controller
        ZMQ.Socket socket = context.createSocket(SocketType.PULL);
        String host = "tcp://localhost:12345";
        // Connect to the Controller PUSH socket
        socket.connect(host);

        System.out.println("Connected to " + host);

        while(!Thread.currentThread().isInterrupted()){
            // wait for next request from the Controller
            String request = socket.recvStr(0);
            System.out.println("Received message: " + new String(request));

            BigInteger[] result = Fermat.fermatFactorization(new BigInteger(request));
            System.out.println(request + " = " + result[0] + " * " + result[1]);

        }
    }

}
