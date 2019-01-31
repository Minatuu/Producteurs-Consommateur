

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface InterfaceProducteur extends Remote {

    public void ReceptionJeton(int val) throws RemoteException;

    void envoyer() throws RemoteException;
}
