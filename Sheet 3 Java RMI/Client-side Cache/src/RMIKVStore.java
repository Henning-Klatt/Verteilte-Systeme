import java.rmi.RemoteException;
import java.util.HashMap;

public class RMIKVStore implements RemoteKVStore {

    private final HashMap<String, String> Store;

    public RMIKVStore() {

        this.Store = new HashMap<String, String>();

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
