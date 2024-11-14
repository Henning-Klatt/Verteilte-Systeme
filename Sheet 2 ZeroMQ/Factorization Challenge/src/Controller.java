import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class Controller extends Thread {

    public static String getChallenge(ZMQ.Socket subscriber){
        while(!Thread.currentThread().isInterrupted()){
            String request = subscriber.recvStr(0);
            System.out.println("[Controller] Received SUB message from Publisher: " + request);
            return request;
        }
        return "";
    }

    public static void sendResult(ZMQ.Socket requestSocket, String result){
        requestSocket.send(result.getBytes(ZMQ.CHARSET), 0);
        byte[] reply = requestSocket.recv(0);
        String respone = new String(reply, ZMQ.CHARSET);
        System.out.println("[Controller] Received REQ message from publisher: " + respone);
    }

    public void run() {

        ZContext context = new ZContext();

        // Create Subscribe Socket to get new challenge from Publisher
        ZMQ.Socket subscriber = context.createSocket(SocketType.SUB);
        subscriber.connect("tcp://vs.lxd-vs.uni-ulm.de:27378");
        subscriber.subscribe("".getBytes());

        // Create Request Socket to send result back to Publisher
        ZMQ.Socket requestSocket = context.createSocket(SocketType.REQ);
        requestSocket.connect("tcp://vs.lxd-vs.uni-ulm.de:27379");

        // Create PUSH socket for worker clients
        ZMQ.Socket socket_push = context.createSocket(SocketType.PUSH);
        socket_push.bind("tcp://*:12345");

        // Create PULL socket for worker clients
        ZMQ.Socket socket_pull = context.createSocket(SocketType.PULL);
        socket_pull.connect("tcp://localhost:12346");

        System.out.println("[Controller connected]");

        while (!Thread.currentThread().isInterrupted()) {
            // Get a new factorization challenge from the publisher
            var challenge = getChallenge(subscriber);

            // Send a message to the workers
            socket_push.send(challenge.getBytes(), 0);

            // Receive the solved factorization from worker
            String request = socket_pull.recvStr(0);
            System.out.println("[Controller] Received PUSH message from worker: " + request);

            // Send solved factorization to publisher
            sendResult(requestSocket, request);
        }
    }
}
