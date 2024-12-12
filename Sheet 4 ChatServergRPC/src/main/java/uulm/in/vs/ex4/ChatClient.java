package uulm.in.vs.ex4;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.util.Scanner;

public class ChatClient {

    private final ChatGrpc.ChatBlockingStub blockingStub;
    private final ChatGrpc.ChatStub chatStub;

    private StreamObserver<ClientMessages> streamObserver;

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

            streamObserver = chatStub.chatStream(new StreamObserver<ChatMessages>() {
                @Override
                public void onNext(ChatMessages chatMessages) {
                    String message = chatMessages.getMessage();
                    String username = chatMessages.getUsername();
                    System.out.println("[" + username + "]: " + message);
                }

                @Override
                public void onError(Throwable throwable) {

                }

                @Override
                public void onCompleted() {
                    streamObserver.onCompleted();
                }
            });

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
            streamObserver.onCompleted();
        } else{
            System.out.println("Logout failed");
        }
    }

    public void listUsers() {
        GetUsersMessage request = GetUsersMessage.newBuilder()
                .setSessionID(this.sessionID)
                .build();



        // UserInfoMessage response = b

        // TODO: implement listUsers
    }

    public void sendMessage(String message) {
        ClientMessages.Builder request = ClientMessages.newBuilder();
        request.setMessage(message);
        request.setSessionID(this.sessionID);
        streamObserver.onNext(request.build());
        request.clear();
    }

    public static void main(String[] args) throws InterruptedException {
        ChatClient client = new ChatClient("localhost", 5555);

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your username: ");
        String username = scanner.nextLine();
        client.login(username);
        System.out.println("Type 'logout' to logout and 'list' to list all online users");
        while(true) {
            System.out.print("> ");
            String message = scanner.nextLine();
            if (message.equals("logout")) {
                client.logout(username);
                break;
            }
            else if (message.equals("list")) {
                client.listUsers();
            } else{
                client.sendMessage(message);
            }
        }
    }
}
