

import java.io.BufferedReader;
import java.io.PrintWriter;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.net.InetAddress;

public class Producteur extends java.rmi.server.UnicastRemoteObject implements Runnable, InterfaceProducteur {

    boolean etat;
    int id;
    private int Npi;
    private int ini;
    private int outi;
    private Object Tpi[];
    private int nbmessPi;
    private int nbauti;
    static int taillep = 5;
    private Producteur successi;
    private String nom;
    InetAddress hote;
    BufferedReader in;
    PrintWriter out;
    public  Object objet =new Object();
    static List<Producteur> listeP = new ArrayList<Producteur>();

    public Producteur(int id) throws RemoteException {
        etat = true;
        this.Npi = 5;
        listeP.add(this);
        this.Tpi = new Object[this.Npi];

        this.id = id;
        this.nbauti = 0;
        this.nbmessPi = 0;
        this.ini = 0;
        this.outi = 0;

    }

    //production et stockage d'un message m
    public void produire(Object m) {
    	synchronized ( objet) {
        if (nbmessPi < Npi) {
            Tpi[ini] = m;
            System.out.println("Producteur "+ this.id+": production de " + m);
            ini = (ini + 1) % Npi;
            nbmessPi++;
        }
    	}
    }

    //reception du jeton
    @Override
    public void ReceptionJeton(int val) throws RemoteException {
        int tempi;

        System.out.println("Producteur " + id + " : reception de jeton " + val);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        synchronized (objet) {
        if (val > 0) {
            tempi = Math.min((nbmessPi - nbauti), val);
            nbauti += tempi;
            val -= nbauti;
        }
        }
        String myIP;
        if (this.getLast() != this) {
            try {
                myIP = InetAddress.getLocalHost().getHostAddress();
                String url = "rmi://" + myIP + ":2001/Producteur" + this.getsucc().id;
                InterfaceProducteur m = (InterfaceProducteur) Naming.lookup(url);
                m.ReceptionJeton(val);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            try {
                myIP = InetAddress.getLocalHost().getHostAddress();
                String url = "rmi://" + myIP + ":2001/Consomateur";
                InterfaceConsomateur m = (InterfaceConsomateur) Naming.lookup(url);
                m.receptionJeton(val);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    
    //envoyer message m au consommateur
    @Override
    public void envoyer() throws RemoteException {
        //while(true)
        //{
    	
        while (nbauti > 0) {
            String myIP;
            try {
                myIP = InetAddress.getLocalHost().getHostAddress();
                String url = "rmi://" + myIP + ":2001/Consomateur";
                InterfaceConsomateur m = (InterfaceConsomateur) Naming.lookup(url);

                m.receptionMess(this.Tpi[this.outi]);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            synchronized(objet) {
            outi = (outi + 1) % Npi;
            nbmessPi--;
            nbauti--;
        }

        }
        //Thread.yield();
        //  }  
    }

    @Override
    public void run() {
        while (true) {
            // TODO Auto-generated method stub
            for (int k = 0; k < 5; k++) {
                this.produire(k);
            
            try {
                Thread.sleep(1000);
                envoyer();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

            }
            }
        }

    }

    public Producteur getsucc() {
        int k = 0;
        for (int i = this.listeP.indexOf(this); i < this.listeP.size(); i++) {
            if (this.listeP.get(i).getEtat()) {
                k = i;
            }
            break;
        }
        return listeP.get((k + 1) % this.listeP.size());
    }

    public boolean getEtat() {
        return etat;
    }

    public Producteur getLast() {
        int k = 0;
        for (int i = this.listeP.size() - 1; i > 0; i--) {
            if (listeP.get(i).getEtat()) {
                k = i;
            }
            break;

        }
        return this.listeP.get(k);
    }

    public static int FirstP() {
        /*int k=0;
		for(int i=0;i>0;i++) {
			if(listeP.get(i).getEtat()) k=i;break;
			
		}*/
        return 0;

    }

}
