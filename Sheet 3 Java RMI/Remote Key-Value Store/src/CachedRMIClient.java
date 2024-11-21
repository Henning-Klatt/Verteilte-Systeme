import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

public class CachedRMIClient implements Subscriber{

    public CachedRMIClient(String hostname, int port) {

        try {
            Registry registry = LocateRegistry.createRegistry(41337);
            SubRMIKVStore skeleton = (SubRMIKVStore) UnicastRemoteObject.exportObject(this, 41338);

            registry.rebind("RMIKVStore", skeleton);

        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateEntry(String key, String value) throws RemoteException {

    }

    @Override
    public void removeEntry(String key) throws RemoteException {

    }

    public void write(String key, String value) {

    }

    public void remove(String key) {

    }

    public String read(String key) {
        // try local cache

        // try remote cache

        return null;
    }
}
