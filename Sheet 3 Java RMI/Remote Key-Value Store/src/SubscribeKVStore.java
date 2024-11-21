import java.rmi.RemoteException;

public interface SubscribeKVStore extends RemoteKVStore{

    void subscribe(String key, Subscriber sub) throws RemoteException;
    void unsubscribe(String key, Subscriber sub) throws RemoteException;

}
