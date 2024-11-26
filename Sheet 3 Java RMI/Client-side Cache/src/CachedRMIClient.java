import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

public class CachedRMIClient extends Thread implements Subscriber{

    // Hash-Map für Key-Value-Store: Key - Value
    private final HashMap<String, String> LocalKVStore;

    private RemoteKVStore remoteKVStore;

    public CachedRMIClient(String hostname, int port) {

        this.LocalKVStore = new HashMap<>();

        try {
            Registry registry = LocateRegistry.getRegistry(hostname,port);

            // Server Stub für einen SubscribeKVStore erzeugen
            SubscribeKVStore skeleton = (SubscribeKVStore) UnicastRemoteObject.exportObject(this, 41339);
            registry.rebind("SubscribeKVStore", skeleton);

            // Entfernter KVStore laden
            remoteKVStore = (RemoteKVStore) registry.lookup("RemoteKVStore");

        } catch (RemoteException e) {
            throw new RuntimeException(e);
        } catch (NotBoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateEntry(String key, String value) throws RemoteException {

    }

    @Override
    public void removeEntry(String key) throws RemoteException {

    }

    public void write(String key, String value) throws RemoteException {
        // lokaler Cache
        LocalKVStore.put(key, value);

        // entfernter KVStore
        remoteKVStore.writeRemote(key, value);
    }

    public void remove(String key) throws RemoteException {
        // lokaler Cache
        LocalKVStore.remove(key);

        // entfernter KVStore
        remoteKVStore.removeRemote(key);
    }

    public String read(String key) {
        // try local cache
        if(LocalKVStore.get(key) != null) {
            return LocalKVStore.get(key);
        }
        // try remote cache
        try {
            return remoteKVStore.readRemote(key);
        } catch (RemoteException e) {
            return null;
        }
    }

    public void run() {
        while(true) {}
    }

}
