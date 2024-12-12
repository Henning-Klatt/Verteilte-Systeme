package uulm.in.vs.ex4;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.UUID;
import java.util.stream.Stream;

public class ChatServer {
    private final static ConcurrentHashMap<String, String> users = new ConcurrentHashMap<>();

    public static class ChatService extends ChatGrpc.ChatImplBase {

        public String getUsernameFromSessionID(String sessionID){
            return users.entrySet()
                    .stream()
                    .filter(entry -> Objects.equals(entry.getValue(), sessionID))
                    .map(Map.Entry::getKey)
                    .findFirst().orElse("");
        }

        @Override
        public void login(LoginRequest request, StreamObserver<LoginResponse> responseObserver) {
            String username = request.getUsername();
            LoginResponse.Builder loginResponse = LoginResponse.newBuilder();
            // Username existiert bereits
            if (users.containsKey(username)) {
                System.out.println("User (" + username + ") is already logged in.");
                loginResponse.setStatus(StatusCode.FAILED);
            }
            // Username ist neu
            else{
                UUID uuid = UUID.randomUUID();
                System.out.println("User (" + username + ") with sessionID: " + uuid.toString() + " logged in.");
                users.put(username, uuid.toString());
                loginResponse.setStatus(StatusCode.OK);
                loginResponse.setSessionID(uuid.toString());
            }
            responseObserver.onNext(loginResponse.build());
            responseObserver.onCompleted();
        }

        @Override
        public void logout(LogoutRequest request, StreamObserver<LogoutResponse> responseObserver) {
            LogoutResponse.Builder logoutResponse = LogoutResponse.newBuilder();
            String username = request.getUsername();
            // Falls username existiert
            if (users.containsKey(username)) {
                String sessionID = request.getSessionID();
                // Prüfe, ob sessionID stimmt
                if(users.get(username).equals(sessionID)) {
                    System.out.println("User (" + username + ") logged out.");
                    users.remove(username);
                    logoutResponse.setStatus(StatusCode.OK);
                    // TODO: terminate client chat stream
                }
                else{
                    System.out.println("Logout sessionID (" + sessionID + ") not found.");
                    logoutResponse.setStatus(StatusCode.FAILED);
                }

            } else{
                System.out.println("Logout User (" + username + ") not found.");
                logoutResponse.setStatus(StatusCode.FAILED);
            }
            responseObserver.onNext(logoutResponse.build());
            responseObserver.onCompleted();
        }

        @Override
        public void listUsers(GetUsersMessage request, StreamObserver<UserInfoMessage> responseObserver) {
            GetUsersMessage.Builder usersMessage = GetUsersMessage.newBuilder();
            String sessionID = request.getSessionID();
            String username = getUsernameFromSessionID(sessionID);
            // Check if user is logged in
            if(users.containsKey(username)) {
                System.out.println("[" + username + "] requested user list");

                // Send user list to single client
                for(Map.Entry<String, String> entry : users.entrySet()) {
                    String user = entry.getKey();

                    UserInfoMessage response = UserInfoMessage.newBuilder()
                            .setUsername(user)
                            .build();
                    responseObserver.onNext(response);
                }
                responseObserver.onCompleted();
            } else{
                System.out.println("User (" + sessionID + ") is not logged in but requested user list");
            }
        }

        @Override
        public StreamObserver<ClientMessages> chatStream(StreamObserver<ChatMessages> responseObserver) {
            return new StreamObserver<ClientMessages>() {
                @Override
                public void onNext(ClientMessages clientMessages) {
                    String sessionID = clientMessages.getSessionID();
                    String message = clientMessages.getMessage();
                    String username = getUsernameFromSessionID(sessionID);
                    // Check if user is logged in
                    if(users.containsKey(username)) {
                        System.out.println("Message received from [" + username + "]: " + message);

                        // Send message to client
                        // TODO: every client, not just this one
                        ChatMessages response = ChatMessages.newBuilder()
                                .setMessage(message)
                                .setUsername(username)
                                .build();

                        responseObserver.onNext(response);

                    } else{
                        System.out.println("Message received from unknown sessionID: [" + sessionID + "]");
                    }
                }

                @Override
                public void onError(Throwable throwable) {

                }

                @Override
                public void onCompleted() {

                }
            };
        }

        // TODO
    }

    public static void main(String[] args) {
        try {
            // Create and start the server
            Server server = ServerBuilder.forPort(5555)
                    .addService(new ChatService())
                    .build()
                    .start();

            // Add a hook to shut the server down if the program is terminated
            Runtime.getRuntime().addShutdownHook(new Thread(server::shutdown));

            // Wait for the server to terminate
            System.out.println("Server started");
            server.awaitTermination();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
