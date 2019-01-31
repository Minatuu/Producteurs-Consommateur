

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface InterfaceMachine extends Remote {

    public void Recevoir(int k) throws java.rmi.RemoteException;

    public int learder() throws java.rmi.RemoteException;

    public void diffuser(int k) throws RemoteException;
}
