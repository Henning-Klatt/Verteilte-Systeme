package uulm.in.vs.ex4;

import com.google.rpc.context.AttributeContext;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.UUID;

public class ChatServer {
    private final static ConcurrentHashMap<String, String> users = new ConcurrentHashMap<>();

    public static class ChatService extends ChatGrpc.ChatImplBase {
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
