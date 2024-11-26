import java.rmi.RemoteException;

public interface Subscriber extends java.rmi.Remote {

    void updateEntry(String key, String value) throws RemoteException;
    void removeEntry(String key) throws RemoteException;

}
