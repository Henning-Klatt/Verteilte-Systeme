import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class Controller {

    public static String getChallenge(){
        ZContext context = new ZContext();
        ZMQ.Socket subscriber = context.createSocket(SocketType.SUB);
        subscriber.connect("tcp://vs.lxd-vs.uni-ulm.de:27378");
        subscriber.subscribe("".getBytes());

        while(!Thread.currentThread().isInterrupted()){
            String request = subscriber.recvStr(0);
            System.out.println("Received SUB message from Publisher: " + request);
            return request;
        }
        return "";
    }

    public static void main(String[] args) throws InterruptedException {

        var challenge = getChallenge();

        ZContext context = new ZContext();

        // Create PUSH socket for Worker clients
        ZMQ.Socket socket = context.createSocket(SocketType.PUSH);
        socket.bind("tcp://*:12345");

        while (true) {
            // Send a message to the workers
            socket.send(challenge, 0);
            Thread.sleep(5000);
        }

    }

}
