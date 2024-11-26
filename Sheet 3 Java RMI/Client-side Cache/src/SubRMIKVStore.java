import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

public class SubRMIKVStore extends Thread implements SubscribeKVStore {

    // Hash-Map für Key-Value-Store: Key - Value
    private final HashMap<String, String> KVStore;
    // Hash-Map für für Subscriptions: Key - Subscriber
    private final HashMap<String, Subscriber> SubsStore;

    public SubRMIKVStore(int port){
        this.KVStore = new HashMap<>();
        this.SubsStore = new HashMap<>();

        try {
            // Einmalige registry auf der einmaligen SubRMIKVStore Instanz
            Registry registry = LocateRegistry.createRegistry(port);
            RemoteKVStore skeleton = (RemoteKVStore) UnicastRemoteObject.exportObject(this, 41338);

            registry.rebind("RemoteKVStore", skeleton);

        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void subscribe(String key, Subscriber sub) throws RemoteException {
        // Prüfe, ob Schlüssel vorhanden ist
        if (SubsStore.get(key) != null) {
            SubsStore.put(key, sub);
        } else{
            throw new RemoteException("Key (" + key + ") not found: Cannot subscribe");
        }
    }

    @Override
    public void unsubscribe(String key, Subscriber sub) throws RemoteException {
        // Prüfe, ob Schlüssel vorhanden ist
        if (SubsStore.get(key) != null) {
            SubsStore.remove(key);
        } else{
            throw new RemoteException("Key (" + key + ") not found: Cannot unsubscribe");
        }
    }

    @Override
    public String readRemote(String key) throws RemoteException {
        if (KVStore.get(key) == null) {
            throw new RemoteException("Key (" + key + ") not found: Cannot readRemote");
        }
        return KVStore.get(key);
    }

    @Override
    public void writeRemote(String key, String value) throws RemoteException {
        System.out.println("[SubRMIKVStore] writeRemote key: " + key + " value: " + value);

        // Alle Clients mit subscription für key aktualisieren
        SubsStore.forEach((Subkey, sub) -> {
            // Falls Subscriber auf key subscribed hat
            if(key.equals(Subkey)){
                // Aktualisiere entfernten Key
                try {
                    sub.updateEntry(key, value);
                    System.out.println("[SubRMIKVStore] entfernten key aktualisiert: " + key + " value: " + value);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    public void removeRemote(String key) throws RemoteException {
        // Alle Clients mit subscription für key aktualisieren
        SubsStore.forEach((Subkey, sub) -> {
            // Falls Subscriber auf key subscribed hat
            if(key.equals(Subkey)){
                // Entferne entfernten Key
                try {
                    sub.removeEntry(key);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public void run() {
        while(true) {}
    }
}
