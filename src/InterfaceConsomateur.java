/*
 * 
 * cette interface contient les methode relative au consomteur 
 * permettent la reception de message 
 * reception jeton 
 */

import java.rmi.*;

public interface InterfaceConsomateur extends Remote {

    public void receptionJeton(int val) throws RemoteException;

    public void receptionMess(Object m) throws RemoteException;
}
