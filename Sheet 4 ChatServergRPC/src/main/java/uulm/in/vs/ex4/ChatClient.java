package uulm.in.vs.ex4;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;

public class ChatClient {

    private final ChatGrpc.ChatBlockingStub blockingStub;
    private final ChatGrpc.ChatStub chatStub;

    String sessionID = null;

    public ChatClient(String host, int port) {

        ManagedChannel channel = ManagedChannelBuilder.forTarget(host + ":" + port)
                .usePlaintext()
                .build();

        blockingStub = ChatGrpc.newBlockingStub(channel);
        chatStub = ChatGrpc.newStub(channel);
    }

    public void login(String username) {
        LoginRequest request = LoginRequest.newBuilder().setUsername(username).build();
        LoginResponse response = blockingStub.login(request);
        if(response.getStatus() == StatusCode.OK) {
            this.sessionID = response.getSessionID();
            System.out.println("Logged in as " + username + " and got sessionID: " + this.sessionID);
        }else{
            System.out.println("Login failed");
        }
    }

    public void logout(String username) {
        LogoutRequest request = LogoutRequest.newBuilder()
                .setUsername(username)
                .setSessionID(this.sessionID)
                .build();

        LogoutResponse response = blockingStub.logout(request);
        if(response.getStatus() == StatusCode.OK) {
            System.out.println("Logged out");
        } else{
            System.out.println("Logout failed");
        }
    }

    public void listUsers() {
        // TODO: implement listUsers
    }

    public void sendMessage(String message) {
        // TODO: implement sendMessage
    }

    public static void main(String[] args) throws InterruptedException {
        ChatClient client = new ChatClient("localhost", 5555);

        String username = "Henning";

        client.login(username);
        Thread.sleep(2000);
        client.logout(username);

    }

}
