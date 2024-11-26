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
        subRMIKVStore.start();


        // zwei CachedRMIClient Instanzen erzeugen
        CachedRMIClient cachedRMIClient_1 = new CachedRMIClient(hostname, port);
        CachedRMIClient cachedRMIClient_2 = new CachedRMIClient(hostname, port);

        cachedRMIClient_1.start();
        cachedRMIClient_2.start();

        try {
            cachedRMIClient_1.write("42", "Die Antwort auf alles");
            // Erwartet: null (Bekommt: null) (weil: noch nicht subscribed)
            System.out.println(cachedRMIClient_2.read("42"));

            subRMIKVStore.subscribe("42", cachedRMIClient_1);
            subRMIKVStore.subscribe("42", cachedRMIClient_2);
            cachedRMIClient_1.write("42", "Die Antwort auf alles");
            // Erwartet: Die Antwort auf alles (Bekommt: Exception Key (42) not found: Cannot subscribe) (weil: :( )
            System.out.println(cachedRMIClient_2.read("42"));

        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

}
