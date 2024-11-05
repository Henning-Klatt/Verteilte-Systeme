import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.Objects;
import java.util.regex.Pattern;

public class NumberGuessingGame {

    public static String getCurrentGameID(ZMQ.Socket socket) {

        socket.send("0:1".getBytes(ZMQ.CHARSET), 0);
        byte[] reply = socket.recv(0);
        String string = new String(reply, ZMQ.CHARSET);
        //System.out.println("Received: [" + string + "]");
        Pattern pattern = Pattern.compile("\\d+");
        return pattern.matcher(string)
                .results()
                .map(mr -> mr.group(0))
                .findFirst()
                .orElse(null);
    }

    public static int getAttempt(String response){
        Pattern pattern = Pattern.compile("Attempt: (\\d+) - ");
        return Integer.parseInt(Objects.requireNonNull(pattern.matcher(response)
                .results()
                .map(mr -> mr.group(1))
                .findFirst()
                .orElse(null)));
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

        long lowerBound = 1;
        long upperBound = (long) Math.pow(2, 63)-1;

        while (true){
            long guess = lowerBound + (upperBound - lowerBound) / 2;
            String request = (currentGameID + ":" + guess);
            //System.out.println("Request: [" + request + "]");
            requestSocket.send(request.getBytes(ZMQ.CHARSET), 0);
            byte[] reply = requestSocket.recv(0);
            String string = new String(reply, ZMQ.CHARSET);
            //System.out.println("Received: [" + string + "]");

            if(string.contains("too small")){
                var attempt = getAttempt(string);
                //System.out.println("Received: [" + string + "]");
                lowerBound = guess + attempt;
            }
            else if(string.contains("too large")){
                var attempt = getAttempt(string);
                //System.out.println("Received: [" + string + "]");
                upperBound = guess + 1;
            }
            else if(string.contains("Correct guess after")){
                System.out.println("Received: [" + string + "]");
                Pattern pattern = Pattern.compile("The new gameID is (\\d+)");
                currentGameID = pattern.matcher(string)
                        .results()
                        .map(mr -> mr.group(1))
                        .findFirst()
                        .orElse(null);
                System.out.println("New gameID: " + currentGameID);
                lowerBound = 1;
                upperBound = (long) Math.pow(2, 63)-1;
            }
            else if(string.contains("GameID unknown!")){
                System.out.println("Received: [" + string + "]");
                currentGameID = getCurrentGameID(requestSocket);
            }
            else if(string.contains("This number has been guessed already.")){
                System.out.println("Received: [" + string + "]");
                currentGameID = getCurrentGameID(requestSocket);
                lowerBound = 1;
                upperBound = (long) Math.pow(2, 63)-1;
            }
        }
    }
}
