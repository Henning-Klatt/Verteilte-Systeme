import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.regex.Pattern;

public class NumberGuessingGame {

    public static String getCurrentGameID(ZMQ.Socket socket) {

        socket.send("0:1".getBytes(ZMQ.CHARSET), 0);
        byte[] reply = socket.recv(0);
        String string = new String(reply, ZMQ.CHARSET);
        System.out.println("Received: [" + string + "]");
        Pattern pattern = Pattern.compile("\\d+");
        return pattern.matcher(string)
                .results()
                .map(mr -> mr.group(0))
                .findFirst()
                .orElse(null);
    }

    public static void main(String[] args){
        String hostName = "vs.lxd-vs.uni-ulm.de";
        int repPort = 27401;

        // Client-Side REQ to Server REP
        ZContext context = new ZContext();
        ZMQ.Socket requestSocket = context.createSocket(SocketType.REQ);
        requestSocket.connect("tcp://" + hostName + ":" + repPort);

        var currentGameID = getCurrentGameID(requestSocket);
        System.out.println("current gameID: " + currentGameID);


    }
}
