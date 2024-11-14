import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

public class RMIKVStore implements RemoteKVStore {

    private final HashMap<String, String> Store;

    public RMIKVStore() {

        this.Store = new HashMap<String, String>();

        try {
            Registry registry = LocateRegistry.createRegistry(41337);
            RemoteKVStore skeleton = (RemoteKVStore) UnicastRemoteObject.exportObject(this, 41338);

            registry.rebind("RMIKVStore", skeleton);

        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public String readRemote(String key) throws RemoteException {
        if (Store.get(key) == null) {
            throw new RemoteException("Key (" + key + ") not found");
        }
        return Store.get(key);
    }

    @Override
    public void writeRemote(String key, String value) throws RemoteException {
        System.out.println("Saving: " + key + ": " + value);
        Store.put(key, value);
    }

    @Override
    public void removeRemote(String key) throws RemoteException {
        Store.remove(key);
    }

    public static void main(String[] args) {
        new RMIKVStore();
    }
}
