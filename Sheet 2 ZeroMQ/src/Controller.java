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

    public static void sendResult(String result){
        ZContext context = new ZContext();
        ZMQ.Socket requestSocket = context.createSocket(SocketType.REQ);
        requestSocket.connect("tcp://vs.lxd-vs.uni-ulm.de:27379");
        requestSocket.send(result.getBytes(ZMQ.CHARSET), 0);
        byte[] reply = requestSocket.recv(0);
        String respone = new String(reply, ZMQ.CHARSET);
        System.out.println("Received REQ message from publisher: " + respone);
    }

    public static void main(String[] args) throws InterruptedException {

        ZContext context = new ZContext();

        // Create PUSH socket for worker clients
        ZMQ.Socket socket_push = context.createSocket(SocketType.PUSH);
        socket_push.bind("tcp://*:12345");

        // Create PULL socket for worker clients
        ZMQ.Socket socket_pull = context.createSocket(SocketType.PULL);
        socket_pull.connect("tcp://localhost:12346");


        while (!Thread.currentThread().isInterrupted()) {
            // Get a new factorization challenge from the publisher
            var challenge = getChallenge();

            // Send a message to the workers
            socket_push.send(challenge.getBytes(), 0);

            // Receive the solved factorization from worker
            String request = socket_pull.recvStr(0);
            System.out.println("Received PUSH message from worker: " + request);

            // Send solved factorization to publisher
            sendResult(request);

            Thread.sleep(2000);
        }
    }
}
