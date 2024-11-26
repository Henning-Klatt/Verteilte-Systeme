import java.rmi.RemoteException;

public class Test {

    public static void main(String[] args) {
        // Registry connection details:
        String hostname = "localhost";
        int port = 41337;

        // Ports für exportierte skeletons
        // RemoteKVStore: 41338
        // SubRMIKVStore: 41339

        // SubRMIKVStore Instanz erzeugen
        SubRMIKVStore subRMIKVStore = new SubRMIKVStore(port);

        // zwei CachedRMIClient Instanzen erzeugen
        CachedRMIClient CachedRMIClient_1 = new CachedRMIClient(hostname, port);
        CachedRMIClient CachedRMIClient_2 = new CachedRMIClient(hostname, port);

        try {
            CachedRMIClient_1.write("42", "Die Antwort auf alles");
            System.out.println(CachedRMIClient_2.read("42"));
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

}
