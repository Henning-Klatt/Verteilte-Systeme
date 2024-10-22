import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

public class EchoClient {
    public static void main(String[] args) throws IOException {

        String hostName = "vs.lxd-vs.uni-ulm.de";
        int port = 3211;

        // TCP Socket
        Socket socket = new Socket(hostName, port);
        OutputStream outStream = socket.getOutputStream();
        InputStream inStream = socket.getInputStream();

        outStream.write("Hello TCP World! (42)\r\n".getBytes());
        outStream.flush();

        String response = "";
        int b;
        while ((b = inStream.read()) != -1) {
            response += (char) b;

            if (response.endsWith("\r\n")) {
                break;
            }
        }
        System.out.println(response);
        outStream.close();
        inStream.close();


        // UDP Socket

        DatagramSocket datagramSocket = new DatagramSocket();
        byte[] input = "Hello UDP World! (42)\r\n".getBytes();
        byte[] output = new byte[input.length];

        DatagramPacket outputPacket = new DatagramPacket(input, input.length, InetAddress.getByName(hostName), port);
        datagramSocket.send(outputPacket);

        DatagramPacket inputPacket = new DatagramPacket(output, output.length);

        while (true) {
            datagramSocket.receive(inputPacket);
            String result = new String(output, 0, inputPacket.getLength());
            System.out.println(result);
            break;
        }
        datagramSocket.close();

    }
}
