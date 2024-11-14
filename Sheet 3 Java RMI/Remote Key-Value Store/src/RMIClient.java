import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RMIClient {
    private RemoteKVStore remoteStore;

    public RMIClient(String hostname, int port) {
        try {
            Registry registry = LocateRegistry.getRegistry(hostname, port);
            remoteStore = (RemoteKVStore) registry.lookup("RMIKVStore");
        } catch (RemoteException e) {
            System.err.println("Registry lookup failed: " + e);
        } catch (NotBoundException e) {
            System.err.println("Service (RMIKVStore) is not available: " + e);
        }
    }

    public void testRemoteStore() {
        try {
            remoteStore.writeRemote("key1", "value1");
            remoteStore.writeRemote("key1", "value2");
            System.out.println(remoteStore.readRemote("key1"));
            remoteStore.writeRemote("key2", "value2");
            remoteStore.removeRemote("key2");
            // read für nicht existierenden Schlüssel
            System.out.println(remoteStore.readRemote("key2"));
            // remove für nicht existierenden Schlüssel
            remoteStore.removeRemote("key2");
        } catch (RemoteException e) {
            //throw new RuntimeException(e);
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        String hostname = "localhost";
        RMIClient client = new RMIClient(hostname, 41337);

        client.testRemoteStore();
    }
}
