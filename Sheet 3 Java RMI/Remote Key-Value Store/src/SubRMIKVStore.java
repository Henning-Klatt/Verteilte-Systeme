import java.rmi.RemoteException;
import java.util.HashMap;

public class SubRMIKVStore implements SubscribeKVStore{

    private final HashMap<String, String> KVStore;
    private final HashMap<String, String> SubsStore;

    public SubRMIKVStore(){
        this.KVStore = new HashMap<>();
        this.SubsStore = new HashMap<>();
    }

    @Override
    public void subscribe(String key, Subscriber sub) throws RemoteException {

    }

    @Override
    public void unsubscribe(String key, Subscriber sub) throws RemoteException {

    }

    @Override
    public String readRemote(String key) throws RemoteException {
        return "";
    }

    @Override
    public void writeRemote(String key, String value) throws RemoteException {
        // Alle Clients mit subscription für key aktualisieren

    }

    @Override
    public void removeRemote(String key) throws RemoteException {
        // Alle Clients mit subscription für key aktualisieren
    }
}
