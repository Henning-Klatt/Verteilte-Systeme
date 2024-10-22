import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ArithmeticExpressionEvaluation {

    public static String parseTCPStream(InputStream inStream) throws IOException {
        String response = "";
        int b;
        while ((b = inStream.read()) != -1) {
            response += (char) b;

            if (response.endsWith("\n")) {
                break;
            }
        }
        return response.replace("\n", "");
    }

    public static int eval(String expression) {
        // Splitte nach + Termen
        String[] addTerms = expression.split("\\+");
        int sum = 0;

        for (String term : addTerms) {
            // Splitte nach * Termen
            String[] factors = term.split("\\*");
            int product = 1;

            // Multipliziere alle * Terme
            for (String factor : factors) {
                product *= Integer.parseInt(factor);
            }

            // Addiere alle fertigen * Terme
            sum += product;
        }
        return sum;
    }

    public static void main(String[] args) throws IOException {

        String hostName = "vs.lxd-vs.uni-ulm.de";
        int port = 5678;

        // TCP Socket
        Socket socket = new Socket(hostName, port);
        OutputStream outStream = socket.getOutputStream();
        InputStream inStream = socket.getInputStream();

        outStream.write("Please provide a new expression!\r\n".getBytes());
        outStream.flush();

        String response = parseTCPStream(inStream);

        System.out.println("Got expression: " + response);

        String result = String.valueOf(eval(response));

        String output_result = response + "=" + result;
        System.out.println(output_result);

        outStream.write((output_result + "\n").getBytes());
        outStream.flush();

        System.out.println(parseTCPStream(inStream));

        outStream.close();
        inStream.close();
    }
}
