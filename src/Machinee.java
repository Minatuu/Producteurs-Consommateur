

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class Machinee extends java.rmi.server.UnicastRemoteObject implements Runnable, InterfaceMachine {

    public enum Etat {
        Repos,
        Encours,
        Termine;
    }
    int suivant;
    Etat etat;
    static List<Machinee> ListeM = new ArrayList<Machinee>();
    int chef;
    int id;
    static int leader;
    static int cpt = 0;
    String ip;

    public Machinee() throws RemoteException {

        etat = Etat.Repos;
        ListeM.add(this);
        id = cpt++;
        chef = id;
    }

    @Override
    public int learder() throws java.rmi.RemoteException {

        if (this.etat == Etat.Repos) {
            this.etat = Etat.Encours;
            this.chef = id;
            String myIP = "";
            try {
                myIP = InetAddress.getLocalHost().getHostAddress();
                String url = "rmi://" + myIP + ":2001/machine" + (this.id + 1) % this.ListeM.size();

                InterfaceMachine im = (InterfaceMachine) Naming.lookup(url);
                im.Recevoir(id);
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
        while (true) {
            if (etat == Etat.Termine) {
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return chef;
    }

    @Override
    public void Recevoir(int k) throws java.rmi.RemoteException {
        if ((etat == Etat.Repos) || (k < chef)) {
            etat = Etat.Encours;
            chef = k;
            String myIP = "";
            try {
                myIP = InetAddress.getLocalHost().getHostAddress();
                String url = "rmi://" + myIP + ":2001/machine" + (this.id + 1) % this.ListeM.size();

                InterfaceMachine im = (InterfaceMachine) Naming.lookup(url);
                im.Recevoir(k);
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        } else {
            if (id == k) {
                etat = Etat.Termine;
                String myIP = "";
                try {
                    myIP = InetAddress.getLocalHost().getHostAddress();
                    String url = "rmi://" + myIP + ":2001/machine" + (this.id + 1) % this.ListeM.size();

                    InterfaceMachine im = (InterfaceMachine) Naming.lookup(url);
                    im.diffuser(id);

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void run() {
        System.out.println("je suis " + id);
        int k = 0;
        try {
            k = this.learder();
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();

        }
        if (k == this.id) {

            leader = k;
            System.out.println( id +" est le Consommateur ");
        } else {

        }
    }

    @Override
    public void diffuser(int k) throws RemoteException {
        // TODO Auto-generated method stub
        if (id != k) {

            String myIP = "";
            try {
                myIP = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            String url = "rmi://" + myIP + ":2001/machine" + (this.id + 1) % this.ListeM.size();

            try {
                InterfaceMachine im = (InterfaceMachine) Naming.lookup(url);
                im.diffuser(k);
            } catch (MalformedURLException | NotBoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            etat = Etat.Termine;

        }

    }

}
