import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.regex.Pattern;

public class NumberGuessingGame {

    enum Result {
        TOO_SMALL,
        TOO_LARGE,
        CORRECT,
        NEW_ID
    }

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

    public static Result sendGuess(ZMQ.Socket socket, String gameID, Long guess) {
        String request = (gameID + ":" + guess);
        System.out.println("Request: [" + request + "]");
        socket.send(request.getBytes(ZMQ.CHARSET), 0);
        byte[] reply = socket.recv(0);
        String string = new String(reply, ZMQ.CHARSET);
        System.out.println("Received: [" + string + "]");

        if(string.contains("too small")){
            return Result.TOO_SMALL;
        }
        else if(string.contains("too large")){
            return Result.TOO_LARGE;
        }
        else if(string.contains("Correct guess after")){
            return Result.CORRECT;
        }
        else if(string.contains("GameID unknown!")){
            return Result.NEW_ID;
        }
        else if(string.contains("This number has been guessed already.")){
            return Result.NEW_ID;
        }
        return null;
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

        mainloop:
        while (true){
            long guess = lowerBound + (upperBound - lowerBound) / 2;
            var result = sendGuess(requestSocket, currentGameID, guess);
            switch(result){
                case TOO_SMALL:
                    lowerBound = guess + 2;
                    break;
                case TOO_LARGE:
                    upperBound = guess + 1;
                    break;
                case CORRECT:
                    currentGameID = getCurrentGameID(requestSocket);
                    lowerBound = 1;
                    upperBound = (long) Math.pow(2, 63)-1;
                    break mainloop;
                case NEW_ID:
                    currentGameID = getCurrentGameID(requestSocket);
                    lowerBound = 1;
                    upperBound = (long) Math.pow(2, 63)-1;
                    break mainloop;
            }
        }
    }
}
